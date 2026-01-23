package edu.ub.pis2425.projecte.presentation.viewmodel.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.features.usecases.exercises.DeleteExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetAllExercisesUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExerciseListViewModel extends ViewModel {
    private final GetAllExercisesUseCase getAllExercisesUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;

    private final MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ExerciseListViewModel(
            GetAllExercisesUseCase getAllExercisesUseCase,
            DeleteExerciseUseCase deleteExerciseUseCase
    ) {
        this.getAllExercisesUseCase = getAllExercisesUseCase;
        this.deleteExerciseUseCase = deleteExerciseUseCase;
    }

    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public void loadExercises() {
        disposables.add(
                getAllExercisesUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> exercises.setValue(list),
                                Throwable::printStackTrace
                        )
        );
    }

    public void deleteExercise(Exercise exercise) {
        disposables.add(
                deleteExerciseUseCase.execute(exercise.getId())
                        .andThen(getAllExercisesUseCase.execute())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> exercises.setValue(list),
                                Throwable::printStackTrace
                        )
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
