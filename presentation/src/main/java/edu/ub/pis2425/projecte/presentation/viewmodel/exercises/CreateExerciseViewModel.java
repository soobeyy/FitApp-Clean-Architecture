package edu.ub.pis2425.projecte.presentation.viewmodel.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.features.usecases.exercises.CreateExerciseUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateExerciseViewModel extends ViewModel {
    private final CreateExerciseUseCase createExerciseUseCase;
    private final MutableLiveData<Boolean> createSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public CreateExerciseViewModel(CreateExerciseUseCase createExerciseUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
    }

    public LiveData<Boolean> getCreateSuccess() {
        return createSuccess;
    }

    public void createExercise(String name, String description, String imageUri) {
        disposables.add(
                createExerciseUseCase.execute(name, description, imageUri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> createSuccess.setValue(true),
                                err -> {
                                    err.printStackTrace();
                                    createSuccess.setValue(false);
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
