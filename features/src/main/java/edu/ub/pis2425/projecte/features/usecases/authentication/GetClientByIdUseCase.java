package edu.ub.pis2425.projecte.features.usecases.authentication;

import edu.ub.pis2425.projecte.domain.entities.Client;
import io.reactivex.rxjava3.core.Observable;

public interface GetClientByIdUseCase {
    Observable<Client> execute();
}
