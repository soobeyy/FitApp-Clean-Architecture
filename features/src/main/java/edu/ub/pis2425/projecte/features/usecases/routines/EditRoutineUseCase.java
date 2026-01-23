package edu.ub.pis2425.projecte.features.usecases.routines;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import io.reactivex.rxjava3.core.Completable;

public interface EditRoutineUseCase {
    Completable execute(Routine routine);
}