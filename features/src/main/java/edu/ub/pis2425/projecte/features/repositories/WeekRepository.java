package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface WeekRepository {
    Completable add(Week week);
    Observable<Week> getById(WeekId id);
    Observable<List<Week>> getById(List<WeekId> ids);
}