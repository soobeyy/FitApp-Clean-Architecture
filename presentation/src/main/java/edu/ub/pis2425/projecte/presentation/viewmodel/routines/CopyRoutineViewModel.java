package edu.ub.pis2425.projecte.presentation.viewmodel.routines;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.features.usecases.routines.DuplicateRoutineUseCase;

public class CopyRoutineViewModel extends ViewModel {
    private final DuplicateRoutineUseCase duplicateRoutineUseCase;
    private final MutableLiveData<Boolean> _copyResult = new MutableLiveData<>();
    public LiveData<Boolean> copyResult = _copyResult;

    public CopyRoutineViewModel(DuplicateRoutineUseCase duplicateRoutineUseCase) {
        this.duplicateRoutineUseCase = duplicateRoutineUseCase;
    }

    /**
     * Llama al caso de uso. Publica true tras la ejecución.
     */
    @SuppressLint("CheckResult")
    public void copyRoutine(String idText) {
        duplicateRoutineUseCase.execute(idText)
                .subscribe(
                        routine -> _copyResult.postValue(true), // Éxito
                        throwable -> _copyResult.postValue(false) // Error
                );
    }
}
