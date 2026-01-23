package edu.ub.pis2425.projecte.features.usecases.authentication;

import io.reactivex.rxjava3.core.Completable;

public interface SignUpUseCase {
    Completable execute(
            String email,
            String password,
            String passwordConfirmation
    );
}
