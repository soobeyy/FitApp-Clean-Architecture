package edu.ub.pis2425.projecte.features.usecases.exercises;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import io.reactivex.rxjava3.core.Observable;

public interface GetExerciseByIdUseCase {
    Observable<Exercise> execute(ExerciseId id);
}
