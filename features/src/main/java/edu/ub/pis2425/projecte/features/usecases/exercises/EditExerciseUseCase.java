package edu.ub.pis2425.projecte.features.usecases.exercises;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import io.reactivex.rxjava3.core.Completable;

public interface EditExerciseUseCase {
    Completable execute(Exercise exercise);
}