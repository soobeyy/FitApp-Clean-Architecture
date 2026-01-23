package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.usecases.routines.EditRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.GetRoutineByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.GetDoneExercisesUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.RateExerciseUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RateRoutineViewModel extends ViewModel {

    private final EditRoutineUseCase editRoutineUseCase;
    private final GetRoutineByIdUseCase getRoutineByIdUseCase;
    private final GetDoneExercisesUseCase getDoneExercisesUseCase;
    private final RateExerciseUseCase rateExerciseUseCase;
    private final CompositeDisposable disposables;
    private final MutableLiveData<Routine> routine;
    private final MutableLiveData<List<Exercise>> selectedExercises;
    private final MutableLiveData<Boolean> updateRoutineResult;
    private final MutableLiveData<List<String>> doneExercises;
    private final MutableLiveData<List<String>> unfinishedExercises;
    private RoutineId currentRoutineId;

    public RateRoutineViewModel(
            EditRoutineUseCase editRoutineUseCase,
            GetRoutineByIdUseCase getRoutineByIdUseCase,
            GetDoneExercisesUseCase getDoneExercisesUseCase,
            RateExerciseUseCase rateExerciseUseCase
    ) {
        this.editRoutineUseCase = editRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
        this.getDoneExercisesUseCase = getDoneExercisesUseCase;
        this.rateExerciseUseCase = rateExerciseUseCase;
        disposables = new CompositeDisposable();
        routine = new MutableLiveData<>();
        selectedExercises = new MutableLiveData<>();
        updateRoutineResult = new MutableLiveData<>();
        doneExercises = new MutableLiveData<>();
        unfinishedExercises = new MutableLiveData<>();
    }

    public LiveData<Routine> getRoutine() {
        return routine;
    }

    public LiveData<List<Exercise>> getSelectedExercises() {
        return selectedExercises;
    }

    public LiveData<Boolean> getUpdateRoutineResult() {
        return updateRoutineResult;
    }

    public LiveData<List<String>> getDoneExercises() {
        return doneExercises;
    }

    public LiveData<List<String>> getUnfinishedExercises() {
        return unfinishedExercises;
    }

    public void setSelectedExercises(List<Exercise> exercises) {
        selectedExercises.setValue(exercises);
    }

    public void loadRoutine(RoutineId routineId, String dayId) {
        if (!routineId.equals(currentRoutineId)) {
            // New routine: update ID and load data
            currentRoutineId = routineId;
            disposables.add(
                    getRoutineByIdUseCase.execute(routineId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    routine -> {
                                        this.routine.setValue(routine);
                                        setSelectedExercises(routine.getExercises());
                                        loadDoneExercises(routineId, dayId);
                                    },
                                    Throwable::printStackTrace
                            )
            );
        } else {
            // Same routine: preserve selectedExercises, update routine and done exercises
            disposables.add(
                    getRoutineByIdUseCase.execute(routineId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    routine -> {
                                        this.routine.setValue(routine);
                                        loadDoneExercises(routineId, dayId);
                                    },
                                    Throwable::printStackTrace
                            )
            );
        }
    }

    private void loadDoneExercises(RoutineId routineId, String dayId) {
        disposables.add(
                getDoneExercisesUseCase.execute(routineId.toString(), dayId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                doneExerciseIds -> {
                                    doneExercises.setValue(doneExerciseIds);
                                    // Update unfinished exercises
                                    Routine currentRoutine = routine.getValue();
                                    if (currentRoutine != null && currentRoutine.getExercises() != null) {
                                        List<String> allExerciseIds = currentRoutine.getExercises().stream()
                                                .map(exercise -> exercise.getId().toString())
                                                .collect(Collectors.toList());
                                        List<String> unfinished = allExerciseIds.stream()
                                                .filter(id -> !doneExerciseIds.contains(id))
                                                .collect(Collectors.toList());
                                        unfinishedExercises.setValue(unfinished);
                                    } else {
                                        unfinishedExercises.setValue(new ArrayList<>());
                                    }
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    public void finishRoutine(String routineId, String dayId) {
        Routine currentRoutine = routine.getValue();
        if (currentRoutine == null) {
            // If routine is not loaded, load it and retry
            loadRoutine(new RoutineId(routineId), dayId);
            return;
        }

        List<String> currentUnfinished = unfinishedExercises.getValue();
        if (currentUnfinished == null || currentUnfinished.isEmpty()) {
            // No unfinished exercises, complete directly
            completeRoutine();
            return;
        }

        // Rate unfinished exercises
        List<Completable> rateCompletable = currentUnfinished.stream()
                .map(exerciseId -> rateExerciseUseCase.execute(exerciseId, dayId)
                        .ignoreElements()) // Convert Observable<Exercise> to Completable
                .collect(Collectors.toList());

        disposables.add(
                Completable.merge(rateCompletable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::completeRoutine,
                                Throwable::printStackTrace
                        )
        );
    }

    private void completeRoutine() {
        // Clear states and prepare for navigation
        clearRoutine();
        updateRoutineResult.setValue(true); // Signal completion for navigation
    }

    public void toggleExerciseSelection(Exercise exercise) {
        List<Exercise> exercises = selectedExercises.getValue();
        if (exercises != null) {
            List<Exercise> updatedExercises = new ArrayList<>(exercises);
            if (updatedExercises.contains(exercise)) {
                updatedExercises.remove(exercise);
            } else {
                updatedExercises.add(exercise);
            }
            selectedExercises.setValue(updatedExercises);
        }
    }

    public void updateRoutine(Routine updated) {
        disposables.add(
                editRoutineUseCase.execute(updated)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> updateRoutineResult.setValue(true),
                                err -> {
                                    err.printStackTrace();
                                    updateRoutineResult.setValue(false);
                                }
                        )
        );
    }

    public List<Serie> getSeriesForExercise(ExerciseId exerciseId, String dayId) {
        Routine currentRoutine = routine.getValue();
        if (currentRoutine == null) {
            return new ArrayList<>();
        }
        for (Exercise exercise : currentRoutine.getExercises()) {
            if (exercise.getId().equals(exerciseId)) {
                return exercise.getValoracion().getOrDefault(dayId, new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }

    public void clearSelectedExercises() {
        selectedExercises.setValue(new ArrayList<>());
    }

    public void clearCreateSuccess() {
        updateRoutineResult.setValue(null);
    }

    public void clearRoutine() {
        routine.setValue(null);
        doneExercises.setValue(null);
        unfinishedExercises.setValue(null);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}