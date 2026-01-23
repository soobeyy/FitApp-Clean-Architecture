package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.EditRoutineUseCase;
import io.reactivex.rxjava3.core.Completable;

public class EditRoutineUseCaseImpl implements EditRoutineUseCase {
    private final RoutineRepository routineRepository;

    public EditRoutineUseCaseImpl(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Override
    public Completable execute(Routine routine) {
        return routineRepository.update(routine);
    }
}