package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import java.util.List;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.GetAllRoutinesUseCase;
import io.reactivex.rxjava3.core.Observable;

public class FetchAllRoutinesUseCaseImpl implements GetAllRoutinesUseCase {
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public FetchAllRoutinesUseCaseImpl(ClientRepository clientRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Observable<List<Routine>> execute() {
        return clientRepository.getById(dataService.getClientId())
                .flatMap(client -> Observable.just(client.getRoutines()));
    }
}