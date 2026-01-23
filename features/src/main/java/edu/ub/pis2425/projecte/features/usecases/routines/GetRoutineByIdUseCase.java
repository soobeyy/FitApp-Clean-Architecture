package edu.ub.pis2425.projecte.features.usecases.routines;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import io.reactivex.rxjava3.core.Observable;

public interface GetRoutineByIdUseCase {
    Observable<Routine> execute(RoutineId id);
}
