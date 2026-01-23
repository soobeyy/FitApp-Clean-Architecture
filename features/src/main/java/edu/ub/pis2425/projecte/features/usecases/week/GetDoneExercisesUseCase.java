package edu.ub.pis2425.projecte.features.usecases.week;

import io.reactivex.rxjava3.core.Observable;
import java.util.List;

public interface GetDoneExercisesUseCase {
    Observable<List<String>> execute(String routineId, String dayId);
}