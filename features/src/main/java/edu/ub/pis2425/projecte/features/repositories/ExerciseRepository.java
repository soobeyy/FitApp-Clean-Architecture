package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface ExerciseRepository {
    Completable add(Exercise exercise);
    Observable<Exercise> getById(ExerciseId id);
    Observable<List<Exercise>> getById(List<ExerciseId> ids);
    Observable<List<Exercise>> getAll();
    Observable<List<Exercise>> getByName(String name);
    Completable remove(ExerciseId id);
    Completable update(Exercise exercise);
    Observable<Exercise> duplicate(ExerciseId id);
    Completable addSerieToExercise(String exerciseId, String dayId, Serie serie);
}
