package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import java.util.List;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.usecases.week.SetRoutinesToDayUseCase;
import io.reactivex.rxjava3.core.Completable;

public class SetRoutinesToDayUseCaseImpl implements SetRoutinesToDayUseCase {

    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public SetRoutinesToDayUseCaseImpl(ClientRepository clientRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Completable execute(String dayId, List<String> routineIds) {
        return clientRepository.setRoutinesForDay(dataService.getClientId(), dayId, routineIds);
    }
}