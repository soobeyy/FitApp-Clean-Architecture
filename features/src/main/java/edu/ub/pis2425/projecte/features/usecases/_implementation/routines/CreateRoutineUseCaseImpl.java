package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import java.util.List;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.CreateRoutineUseCase;
import io.reactivex.rxjava3.core.Completable;

public class CreateRoutineUseCaseImpl implements CreateRoutineUseCase {
    private final RoutineRepository routineRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public CreateRoutineUseCaseImpl(RoutineRepository routineRepository, ClientRepository clientRepository, IDataService dataService) {
        this.routineRepository = routineRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Completable execute(String name, String description, List<Exercise> exercises) {
        Routine routine = Routine.createRoutine(name, description, exercises);
        return routineRepository.add(routine)
                .andThen(Completable.defer(() -> clientRepository.addRoutine(dataService.getClientId(), routine.getId().toString())));
    }
}