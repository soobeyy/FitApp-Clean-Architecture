package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface RoutineRepository {

    Completable add(Routine routine);
    Observable<Routine> getById(RoutineId id);
    Observable<List<Routine>> getById(List<RoutineId> ids);
    Completable update(Routine routine);
    Completable delete(RoutineId id);
    Observable<Routine> duplicate(RoutineId routineId);
}
