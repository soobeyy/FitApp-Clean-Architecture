package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.exercises.DuplicateExerciseUseCase;
import io.reactivex.rxjava3.core.Observable;

public class DuplicateExerciseUseCaseImpl implements DuplicateExerciseUseCase {
    private final ExerciseRepository exerciseRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public DuplicateExerciseUseCaseImpl(ExerciseRepository exerciseRepository, ClientRepository clientRepository, IDataService dataService) {
        this.exerciseRepository = exerciseRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Observable<Exercise> execute(String id) {
        return exerciseRepository
                .duplicate(new ExerciseId(id))
                .flatMap(duplicatedExercise ->
                        clientRepository
                                .addExercise(dataService.getClientId(), duplicatedExercise.getId())
                                .andThen(Observable.just(duplicatedExercise))
                );
    }
}