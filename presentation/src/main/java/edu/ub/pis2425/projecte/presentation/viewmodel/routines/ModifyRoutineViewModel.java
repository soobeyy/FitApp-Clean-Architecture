package edu.ub.pis2425.projecte.presentation.viewmodel.routines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.usecases.routines.EditRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.GetRoutineByIdUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class ModifyRoutineViewModel extends ViewModel {

    private final EditRoutineUseCase editRoutineUseCase;
    private final GetRoutineByIdUseCase getRoutineByIdUseCase;
    private final CompositeDisposable disposables;
    private final MutableLiveData<Routine> routine;
    private final MutableLiveData<List<Exercise>> selectedExercises;
    private final MutableLiveData<Boolean> updateRoutineResult;
    private RoutineId currentRoutineId;

    public ModifyRoutineViewModel(EditRoutineUseCase editRoutineUseCase, GetRoutineByIdUseCase getRoutineByIdUseCase) {
        this.editRoutineUseCase = editRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
        disposables = new CompositeDisposable();
        routine = new MutableLiveData<>();
        selectedExercises = new MutableLiveData<>();
        updateRoutineResult = new MutableLiveData<>();
    }

    public LiveData<Routine> getRoutine() {
        return routine;
    }

    public void setSelectedExercises(List<Exercise> exercises) {
        selectedExercises.setValue(exercises);
    }

    public LiveData<List<Exercise>> getSelectedExercises() {
        return selectedExercises;
    }

    public LiveData<Boolean> getUpdateRoutineResult() {
        return updateRoutineResult;
    }

    public void loadRoutine(RoutineId routineId) {
        if (!routineId.equals(currentRoutineId)) {
            // Nueva rutina: actualizar el ID y cargar sus datos
            currentRoutineId = routineId;
            disposables.add(
                    getRoutineByIdUseCase.execute(routineId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    routine -> {
                                        this.routine.setValue(routine);
                                        setSelectedExercises(routine.getExercises()); // Actualizar ejercicios seleccionados
                                    },
                                    Throwable::printStackTrace
                            )
            );
        } else {
            // Misma rutina: no sobrescribir selectedExercises, preservar los cambios
            disposables.add(
                    getRoutineByIdUseCase.execute(routineId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    routine -> this.routine.setValue(routine),
                                    Throwable::printStackTrace
                            )
            );
        }
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

    public void clearSelectedExercises() {
        selectedExercises.setValue(new ArrayList<>());
    }

    public void clearCreateSuccess() {
        updateRoutineResult.setValue(null);
    }

    public void clearRoutine() {
        routine.setValue(null);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}