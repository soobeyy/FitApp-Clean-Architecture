package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.exercises.EditExerciseUseCase;
import io.reactivex.rxjava3.core.Completable;

public class EditExerciseUseCaseImpl implements EditExerciseUseCase {
    private final ExerciseRepository exerciseRepository;

    public EditExerciseUseCaseImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Completable execute(Exercise exercise) {
        return exerciseRepository.update(exercise);
    }
}