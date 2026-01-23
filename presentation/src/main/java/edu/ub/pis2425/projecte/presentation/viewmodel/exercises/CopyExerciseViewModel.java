package edu.ub.pis2425.projecte.presentation.viewmodel.exercises;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.features.usecases.exercises.DuplicateExerciseUseCase;


public class CopyExerciseViewModel extends ViewModel {

    private final DuplicateExerciseUseCase duplicateExerciseUseCase;
    private final MutableLiveData<Boolean> _duplicateResult = new MutableLiveData<>();
    public LiveData<Boolean> duplicateResult = _duplicateResult;

    public CopyExerciseViewModel(DuplicateExerciseUseCase duplicateExerciseUseCase) {
        this.duplicateExerciseUseCase = duplicateExerciseUseCase;
    }

    /**
     * Llama al caso de uso. Publica true si funciona sin excepción, false si falla.
     */
    @SuppressLint("CheckResult")
    public void duplicateExercise(String idText) {
        duplicateExerciseUseCase.execute(idText)
                .subscribe(
                        exercise -> _duplicateResult.postValue(true), // Éxito
                        throwable -> _duplicateResult.postValue(false) // Error
                );
    }
}
