package edu.ub.pis2425.projecte.features.usecases._implementation.exercises;

import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.usecases.exercises.CreateExerciseUseCase;
import io.reactivex.rxjava3.core.Completable;

public class CreateExerciseUseCaseImpl implements CreateExerciseUseCase {

    private final ExerciseRepository exerciseRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public CreateExerciseUseCaseImpl(ExerciseRepository exerciseRepository, ClientRepository clientRepository, IDataService dataService) {
        this.exerciseRepository = exerciseRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
    }

    @Override
    public Completable execute(String name, String description, String image) {
        Exercise exercise = Exercise.createExercise(name, description, image);
        return exerciseRepository.add(exercise)
                .andThen(Completable.defer(() ->
                        clientRepository.addExercise(dataService.getClientId(), exercise.getId())
                ));
    }
}