package edu.ub.pis2425.projecte.features.usecases._implementation.authentication;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogOutUseCase;
import io.reactivex.rxjava3.core.Completable;

public class LogOutUseCaseImpl implements LogOutUseCase {

    private final IDataService dataService;

    public LogOutUseCaseImpl(IDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Completable execute() {
        return Completable.fromAction(dataService::clearSession);
    }
}