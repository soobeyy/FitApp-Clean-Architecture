package edu.ub.pis2425.projecte.features.usecases.week;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import io.reactivex.rxjava3.core.Observable;

public interface GetRoutinesFromDayUseCase {
    Observable<List<Routine>> execute(String dayId);
}
