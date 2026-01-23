package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.DeleteRoutineUseCase;
import io.reactivex.rxjava3.core.Completable;

public class DeleteRoutineUseCaseImpl implements DeleteRoutineUseCase {
    private final RoutineRepository routineRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public DeleteRoutineUseCaseImpl(RoutineRepository routineRepository, ClientRepository clientRepository, IDataService dataService) {
        this.routineRepository = routineRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    public Completable execute(RoutineId id) {
        return routineRepository.delete(id)
                .andThen(clientRepository.removeRoutine(dataService.getClientId(), id.getId()));
    }
}