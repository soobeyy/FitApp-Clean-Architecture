package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.features.usecases.exercises.EditExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetExerciseByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.RateExerciseUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RateExerciseViewModel extends ViewModel {
    private final GetExerciseByIdUseCase getByIdUseCase;
    private final EditExerciseUseCase editUseCase;
    private final RateExerciseUseCase rateUseCase;

    private final MutableLiveData<Exercise> exercise = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public RateExerciseViewModel(
            GetExerciseByIdUseCase getByIdUseCase,
            EditExerciseUseCase editUseCase,
            RateExerciseUseCase rateUseCase
    ) {
        this.getByIdUseCase = getByIdUseCase;
        this.editUseCase = editUseCase;
        this.rateUseCase = rateUseCase;
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public void rateExercise(String exerciseId, String dayId) {
        disposables.add(
                rateUseCase.execute(exerciseId, dayId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                exercise::setValue,
                                Throwable::printStackTrace
                        )
        );

    }

    /** Llamar desde el Fragment con el ID recibido */
    public void loadExercise(ExerciseId id) {
        disposables.add(
                getByIdUseCase.execute(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                exercise::setValue,
                                Throwable::printStackTrace
                        )
        );
    }

    public void updateExercise(Exercise updated) {
        disposables.add(
                editUseCase.execute(updated)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> updateSuccess.setValue(true),
                                err -> {
                                    err.printStackTrace();
                                    updateSuccess.setValue(false);
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
