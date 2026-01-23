package edu.ub.pis2425.projecte.features.usecases._implementation.authentication;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogInUseCase;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class LogInUseCaseImpl implements LogInUseCase {
    /* Attributes */
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    /**
     * Constructor
     */
    public LogInUseCaseImpl(ClientRepository clientRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    /**
     * Log in client with username and password.
     * @param clientId the username
     * @param enteredPassword the password
     */
    public Observable<Client> execute(ClientId clientId, String enteredPassword) {
        return validateInputs(clientId, enteredPassword)
                .andThen(clientRepository.getById(clientId))
                .concatMap(client -> {
                    if (client == null) {
                        return Observable.error(new Throwable("Cliente no encontrado"));
                    }
                    return tryLogIn(client, enteredPassword);
                });
    }

    /* Private methods */

    /**
     * Validate the inputs.
     * @param clientId the username
     * @param password the password
     * @return a completable
     */
    private Completable validateInputs(ClientId clientId, String password) {
        return Completable.fromCallable(() -> {
            if (clientId == null || clientId.getId().isEmpty())
                throw new IllegalArgumentException("Es obligatorio poner un correo electrónico");
            if (password == null || password.isEmpty())
                throw new IllegalArgumentException("Es obligatorio poner una contraseña");
            return Completable.complete();
        });
    }

    /**
     * Try to log in a client.
     * @param client the client
     * @param enteredPassword the password
     * @return an observable
     */
    private Observable<Client> tryLogIn(
            Client client,
            String enteredPassword
    ) {
        return Observable.defer(() -> {
            if (client.getPassword().equals(enteredPassword)) {
                dataService.setClient(client);
                return Observable.just(client);
            } else {
                return Observable.error(new Throwable("Contraseña inválida"));
            }
        });
    }
}