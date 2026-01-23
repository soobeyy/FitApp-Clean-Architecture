package edu.ub.pis2425.projecte.features.usecases.routines;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import io.reactivex.rxjava3.core.Completable;

public interface CreateRoutineUseCase {
    Completable execute(String name,
                        String description,
                        List<Exercise> exercises);
}
