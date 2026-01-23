package edu.ub.pis2425.projecte.presentation.viewmodel.routines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.features.usecases.routines.CreateRoutineUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateRoutineViewModel extends ViewModel {

    private final CreateRoutineUseCase createRoutineUseCase;
    private MutableLiveData<Boolean> createSuccess;
    private final MutableLiveData<String> errorMessage;
    private final CompositeDisposable disposables;
    private final MutableLiveData<List<Exercise>> selectedExercises;

    public CreateRoutineViewModel(CreateRoutineUseCase createRoutineUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
        this.createSuccess = new MutableLiveData<>();
        this.errorMessage = new MutableLiveData<>();
        this.disposables = new CompositeDisposable();
        this.selectedExercises = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<Boolean> getCreateSuccess() {
        return createSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Exercise>> getSelectedExercises() {
        return selectedExercises;
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
        } else {
            List<Exercise> newExercises = new ArrayList<>();
            newExercises.add(exercise);
            selectedExercises.setValue(newExercises);
        }
    }

    public void createRoutine(String name, String description) {
        List<Exercise> exercises = selectedExercises.getValue();

        disposables.add(
                createRoutineUseCase.execute(name, description, exercises)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> createSuccess.setValue(true),
                                err -> {
                                    err.printStackTrace();
                                    errorMessage.setValue("Error desconocido al crear la rutina");
                                }
                        )
        );
    }

    public void clearCreateSuccess() {
        createSuccess = new MutableLiveData<>();
    }

    public void clearSelectedExercises() {
        selectedExercises.setValue(new ArrayList<>());
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
    public void setSelectedExercises(List<Exercise> exercises) {
        selectedExercises.setValue(new ArrayList<>(exercises));
    }
}
