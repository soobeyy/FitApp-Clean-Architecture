package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetAllExercisesUseCase;

public class FetchAllExercisesUseCaseImpl implements GetAllExercisesUseCase {
    /* Attributes */
    private final IDataService dataService;
    private final ClientRepository clientRepository;

    /**
     * Constructor
     */
    public FetchAllExercisesUseCaseImpl(ClientRepository clientRepository, IDataService dataService) {
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    /**
     * Get all exercises.
     */
    public Observable<List<Exercise>> execute() {
        return clientRepository.getById(dataService.getClientId())
                .flatMap(client -> Observable.just(client.getExercises()));
    }
}