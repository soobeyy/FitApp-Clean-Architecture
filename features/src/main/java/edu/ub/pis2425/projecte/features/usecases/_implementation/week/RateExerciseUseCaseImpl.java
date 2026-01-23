package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import java.util.ArrayList;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.week.RateExerciseUseCase;
import io.reactivex.rxjava3.core.Observable;

public class RateExerciseUseCaseImpl implements RateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;

    public RateExerciseUseCaseImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Observable<Exercise> execute(String exerciseId, String dayId) {
        return exerciseRepository.getById(new ExerciseId(exerciseId))
                .flatMap(exercise -> {
                    // Ensure the exercise has a valoracion HashMap
                    if (exercise.getValoracion() == null) {
                        exercise.setValoracion(new java.util.HashMap<>());
                    }

                    // Add the day ID with an empty ArrayList to the valoracion HashMap
                    exercise.getValoracion().put(dayId, new ArrayList<>());

                    // Update the exercise in the repository and emit the updated exercise
                    return exerciseRepository.update(exercise)
                            .andThen(Observable.just(exercise));
                });
    }
}