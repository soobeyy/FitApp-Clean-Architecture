package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.features.usecases.week.SetRoutinesToDayUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.GetRoutinesFromDayUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SelectRoutineViewModel extends ViewModel {
    private final SetRoutinesToDayUseCase setRoutinesToDayUseCase;
    private final GetRoutinesFromDayUseCase getRoutinesFromDayUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> addSuccess = new MutableLiveData<>();
    private final MutableLiveData<List<Routine>> selectedRoutines = new MutableLiveData<>();

    public SelectRoutineViewModel(SetRoutinesToDayUseCase setRoutinesToDayUseCase, GetRoutinesFromDayUseCase getRoutinesFromDayUseCase) {
        this.setRoutinesToDayUseCase = setRoutinesToDayUseCase;
        this.getRoutinesFromDayUseCase = getRoutinesFromDayUseCase;
    }

    public LiveData<List<Routine>> getSelectedRoutines() {
        return selectedRoutines;
    }

    public void loadSelectedRoutines(String dayId ) {
        disposables.add(
                getRoutinesFromDayUseCase.execute(dayId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                selectedRoutines::setValue,
                                Throwable::printStackTrace
                        )
        );
    }

    public LiveData<Boolean> getAddSuccess() {
        return addSuccess;
    }

    public void addRoutinesToDay(String dayId, List<String> routineIds) {
        disposables.add(
                setRoutinesToDayUseCase.execute(dayId, routineIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    addSuccess.setValue(true);

                                },
                                err -> {
                                    err.printStackTrace();
                                    addSuccess.setValue(false);
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}