package edu.ub.pis2425.projecte.features.usecases.routines;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import io.reactivex.rxjava3.core.Observable;

public interface DuplicateRoutineUseCase {
    Observable<Routine> execute(String id);
}
