package edu.ub.pis2425.projecte.features.usecases.week;

import edu.ub.pis2425.projecte.domain.entities.Sensation;
import io.reactivex.rxjava3.core.Completable;

public interface RateSerieUseCase {

    Completable execute(int kg, int reps, Sensation sensation, String dayId, String exerciseId);
}
