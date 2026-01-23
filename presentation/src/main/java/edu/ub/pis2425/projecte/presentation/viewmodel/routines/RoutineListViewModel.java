package edu.ub.pis2425.projecte.presentation.viewmodel.routines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.features.usecases.routines.DeleteRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.GetAllRoutinesUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RoutineListViewModel extends ViewModel {
    private final GetAllRoutinesUseCase getAllRoutinesUseCase;
    private final MutableLiveData<List<Routine>> routines = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final DeleteRoutineUseCase deleteRoutineUseCase;

    public RoutineListViewModel(
            GetAllRoutinesUseCase getAllRoutinesUseCase,
            DeleteRoutineUseCase deleteRoutineUseCase
    ) {
        this.getAllRoutinesUseCase = getAllRoutinesUseCase;
        this.deleteRoutineUseCase = deleteRoutineUseCase;
    }



    public LiveData<List<Routine>> getRoutines() {
        return routines;
    }

    public void loadRoutines() {
        disposables.add(
                getAllRoutinesUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> routines.setValue(list),
                                Throwable::printStackTrace
                        )
        );
    }
    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
    public void deleteRoutine(Routine routine) {
        disposables.add(
                deleteRoutineUseCase.execute(routine.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::loadRoutines,
                                Throwable::printStackTrace
                        )
        );
    }

}