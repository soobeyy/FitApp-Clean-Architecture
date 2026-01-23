package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface DayRepository {
    Completable add(Day day);
    Observable<Day> getById(DayId id);
    Observable<List<Day>> getById(List<DayId> ids);
    Completable addRoutinesToDay(String dayId, List<String> routineIds);
}