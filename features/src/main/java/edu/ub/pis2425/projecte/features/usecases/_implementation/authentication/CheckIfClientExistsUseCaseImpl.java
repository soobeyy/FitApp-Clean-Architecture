package edu.ub.pis2425.projecte.features.usecases._implementation.authentication;

import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.authentication.CheckIfClientExistsUseCase;
import io.reactivex.rxjava3.core.Observable;

public class CheckIfClientExistsUseCaseImpl implements CheckIfClientExistsUseCase {
    private final ClientRepository clientRepository;

    public CheckIfClientExistsUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Observable<Boolean> execute(ClientId clientId) {
        return clientRepository.getById(clientId)
                .map(ignoredClient -> true)
                .onErrorReturnItem(false);
    }
}
