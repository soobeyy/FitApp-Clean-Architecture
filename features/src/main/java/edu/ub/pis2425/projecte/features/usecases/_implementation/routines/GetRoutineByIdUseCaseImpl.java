package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.GetRoutineByIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetRoutineByIdUseCaseImpl implements GetRoutineByIdUseCase {
    RoutineRepository routineRepository;

    public GetRoutineByIdUseCaseImpl(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Override
    public Observable<Routine> execute(RoutineId id) {
        return routineRepository.getById(id);
    }
}
