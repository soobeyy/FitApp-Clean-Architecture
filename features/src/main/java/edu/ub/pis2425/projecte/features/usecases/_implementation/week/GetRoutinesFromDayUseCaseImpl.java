package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import java.util.List;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import edu.ub.pis2425.projecte.features.usecases.week.GetRoutinesFromDayUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetRoutinesFromDayUseCaseImpl implements GetRoutinesFromDayUseCase {
    private final ClientRepository clientRepository;
    private final DayRepository dayRepository;
    private final IDataService dataService;

    public GetRoutinesFromDayUseCaseImpl(ClientRepository clientRepository, DayRepository dayRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dayRepository = dayRepository;
        this.dataService = dataService;
    }

    public Observable<List<Routine>> execute(String dayId) {
        Observable<Client> clientObservable = clientRepository.getById(dataService.getClientId());
        Observable<Day> dayObservable = dayRepository.getById(new DayId(dayId));

        return Observable.zip(clientObservable, dayObservable, (client, day) -> client.getRoutinesFromDay(day));
    }
}