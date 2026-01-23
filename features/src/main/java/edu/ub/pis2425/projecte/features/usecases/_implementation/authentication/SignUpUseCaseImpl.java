package edu.ub.pis2425.projecte.features.usecases._implementation.authentication;

import java.time.LocalDate;

import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.authentication.CheckIfClientExistsUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.SignUpUseCase;
import io.reactivex.rxjava3.core.Completable;

public class SignUpUseCaseImpl implements SignUpUseCase {

    private final ClientRepository clientRepository;
    private final CheckIfClientExistsUseCase checkIfClientExistsUseCase;

    public SignUpUseCaseImpl(ClientRepository clientRepository, CheckIfClientExistsUseCase checkIfClientExistsUseCase) {
        this.clientRepository = clientRepository;
        this.checkIfClientExistsUseCase = checkIfClientExistsUseCase;
    }

    public Completable execute(String email, String password, String passwordConfirmation) {
        return Completable.fromCallable(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Es obligatorio introducir un correo electrónico");
            }
            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("El formato del correo electrónico no es válido");
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Es obligatorio introducir una contraseña");
            }
            if (!password.equals(passwordConfirmation)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            return true;
        }).andThen(
                checkIfClientExistsUseCase.execute(new ClientId(email))
                        .map(exists -> {
                            if (exists) {
                                throw new IllegalArgumentException("Este correo electrónico ya está registrado");
                            }
                            return Client.createClient(email, password, passwordConfirmation, LocalDate.now());
                        })
                        .flatMapCompletable(client ->
                                clientRepository.add(client)
                                        .andThen(clientRepository.addBasic(client.getId()))
                        )
        );
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }
}