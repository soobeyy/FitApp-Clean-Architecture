package edu.ub.pis2425.projecte.features.usecases.week;


import java.util.List;

import io.reactivex.rxjava3.core.Completable;

public interface SetRoutinesToDayUseCase {
    Completable execute(String dayId, List<String> routineId);
}
