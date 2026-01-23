package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetExerciseByIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetExerciseByIdUseCaseImpl implements GetExerciseByIdUseCase {
    private final ExerciseRepository exerciseRepository;

    public GetExerciseByIdUseCaseImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Observable<Exercise> execute(ExerciseId id) {
        return exerciseRepository.getById(id);
    }
}

