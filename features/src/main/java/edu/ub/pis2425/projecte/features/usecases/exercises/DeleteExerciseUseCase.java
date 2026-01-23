package edu.ub.pis2425.projecte.features.usecases.exercises;

import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import io.reactivex.rxjava3.core.Completable;

public interface DeleteExerciseUseCase {
    Completable execute(ExerciseId exerciseId);
}
