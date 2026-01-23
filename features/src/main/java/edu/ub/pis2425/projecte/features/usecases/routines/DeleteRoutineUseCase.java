package edu.ub.pis2425.projecte.features.usecases.routines;

import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import io.reactivex.rxjava3.core.Completable;

public interface DeleteRoutineUseCase {
    Completable execute(RoutineId routineId);
}
