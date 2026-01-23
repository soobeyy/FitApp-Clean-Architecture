package edu.ub.pis2425.projecte.features.usecases.exercises;

import io.reactivex.rxjava3.core.Completable;

public interface CreateExerciseUseCase {
    Completable execute(
            String name,
            String description,
            String imageUri
    );
}
