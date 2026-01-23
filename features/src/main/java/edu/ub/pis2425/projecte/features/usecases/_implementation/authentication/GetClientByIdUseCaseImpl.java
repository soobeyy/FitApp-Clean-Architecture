package edu.ub.pis2425.projecte.features.usecases._implementation.authentication;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.authentication.GetClientByIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetClientByIdUseCaseImpl implements GetClientByIdUseCase {
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public GetClientByIdUseCaseImpl(ClientRepository clientRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Observable<Client> execute() {
        return clientRepository.getById(dataService.getClientId())
                .concatMap(client -> {
                    if (client == null) {
                        return Observable.error(new Throwable("Cliente no encontrado"));
                    }
                    return Observable.just(client);
                });
    }
}
