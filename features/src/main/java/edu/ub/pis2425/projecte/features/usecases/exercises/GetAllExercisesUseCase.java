package edu.ub.pis2425.projecte.features.usecases.exercises;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import io.reactivex.rxjava3.core.Observable;

public interface GetAllExercisesUseCase {
    Observable<List<Exercise>> execute();
}
