package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.exercises.DeleteExerciseUseCase;
import io.reactivex.rxjava3.core.Completable;

public class DeleteExerciseUseCaseImpl implements DeleteExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public DeleteExerciseUseCaseImpl(ExerciseRepository exerciseRepository, ClientRepository clientRepository, IDataService dataService) {
        this.exerciseRepository = exerciseRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Completable execute(ExerciseId exerciseId) {
        return exerciseRepository.remove(exerciseId)
                .andThen(clientRepository.removeExercise(dataService.getClientId(), exerciseId));
    }
}