package edu.ub.pis2425.projecte.features.usecases.week;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import io.reactivex.rxjava3.core.Observable;

public interface RateExerciseUseCase {
    Observable<Exercise> execute(String exerciseId, String dayId);
}
