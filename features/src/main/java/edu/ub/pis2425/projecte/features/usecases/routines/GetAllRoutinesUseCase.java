package edu.ub.pis2425.projecte.features.usecases.routines;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import io.reactivex.rxjava3.core.Observable;

public interface GetAllRoutinesUseCase {
    /**
     * Get all routines.
     *
     * @return Observable with the list of routines
     */
    Observable<List<Routine>> execute();
}
