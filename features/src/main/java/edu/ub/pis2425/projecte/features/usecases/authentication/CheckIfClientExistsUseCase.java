package edu.ub.pis2425.projecte.features.usecases.authentication;

import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import io.reactivex.rxjava3.core.Observable;

public interface CheckIfClientExistsUseCase {
    Observable<Boolean> execute(ClientId clientId);
}
