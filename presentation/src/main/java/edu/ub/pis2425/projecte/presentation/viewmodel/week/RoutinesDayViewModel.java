package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.usecases.week.GetDoneExercisesUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.GetRoutinesFromDayUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.SetRoutinesToDayUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RoutinesDayViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<Routine>> routines = new MutableLiveData<>();
    private final GetRoutinesFromDayUseCase getRoutinesFromDayUseCase;
    private final GetDoneExercisesUseCase getDoneExercisesUseCase;
    private final MutableLiveData<List<String>> doneRoutines = new MutableLiveData<>();
    private final SetRoutinesToDayUseCase setRoutinesForDay;
    // Assuming you need these for the original loadDoneExercises, but we'll modify its usage
    private final MutableLiveData<List<String>> doneExercises = new MutableLiveData<>();
    private final MutableLiveData<List<String>> unfinishedExercises = new MutableLiveData<>();

    public RoutinesDayViewModel(GetRoutinesFromDayUseCase getRoutinesFromDayUseCase, SetRoutinesToDayUseCase setRoutinesForDay, GetDoneExercisesUseCase getDoneExercisesUseCase) {
        this.getRoutinesFromDayUseCase = getRoutinesFromDayUseCase;
        this.setRoutinesForDay = setRoutinesForDay;
        this.getDoneExercisesUseCase = getDoneExercisesUseCase;
    }

    public MutableLiveData<List<Routine>> getRoutines() {
        return routines;
    }

    public LiveData<List<String>> getDoneRoutines() {
        return doneRoutines;
    }

    public void loadRoutines(String dayId) {
        disposables.add(
                getRoutinesFromDayUseCase.execute(dayId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                routineList -> {
                                    routines.setValue(routineList);
                                    // After loading routines, check completion status for each
                                    checkAllRoutinesCompletion(dayId, routineList);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    private void checkAllRoutinesCompletion(String dayId, List<Routine> routineList) {
        List<String> completedRoutineIds = new ArrayList<>();
        if (routineList == null || routineList.isEmpty()) {
            doneRoutines.setValue(completedRoutineIds);
            return;
        }

        disposables.add(
                Observable.fromIterable(routineList)
                        .flatMap(routine -> {
                            // Skip routines with no exercises (consider them incomplete)
                            if (routine.getExercises() == null || routine.getExercises().isEmpty()) {
                                return Observable.just(new Pair<>(routine.getId().toString(), false));
                            }

                            // Fetch done exercises for the routine
                            return getDoneExercisesUseCase.execute(routine.getId().toString(), dayId)
                                    .map(doneExerciseIds -> {
                                        List<String> allExerciseIds = routine.getExercises().stream()
                                                .map(exercise -> exercise.getId().toString())
                                                .collect(Collectors.toList());

                                        // Update done and unfinished exercises for compatibility
                                        doneExercises.setValue(doneExerciseIds);
                                        List<String> unfinished = allExerciseIds.stream()
                                                .filter(id -> !doneExerciseIds.contains(id))
                                                .collect(Collectors.toList());
                                        unfinishedExercises.setValue(unfinished);

                                        // Routine is complete if all exercises are done
                                        boolean isComplete = !allExerciseIds.isEmpty() && doneExerciseIds.containsAll(allExerciseIds);
                                        return new Pair<>(routine.getId().toString(), isComplete);
                                    })
                                    .onErrorReturn(throwable -> {
                                        throwable.printStackTrace();
                                        return new Pair<>(routine.getId().toString(), false); // Assume incomplete on error
                                    });
                        })
                        .collectInto(new ArrayList<Pair<String, Boolean>>(), (list, pair) -> {
                            list.add(pair);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                pairs -> {
                                    // Extract IDs of completed routines
                                    List<String> completedIds = pairs.stream()
                                            .filter(pair -> pair.second)
                                            .map(pair -> pair.first)
                                            .collect(Collectors.toList());
                                    doneRoutines.setValue(completedIds);
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    doneRoutines.setValue(new ArrayList<>()); // Empty list on error
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public Completable setRoutinesForDay(String dayId, List<String> routineIds) {
        return setRoutinesForDay.execute(dayId, routineIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Interface to handle async completion check
    interface OnCompletionCheckedListener {
        void onCompletionChecked(boolean isComplete);
    }
}