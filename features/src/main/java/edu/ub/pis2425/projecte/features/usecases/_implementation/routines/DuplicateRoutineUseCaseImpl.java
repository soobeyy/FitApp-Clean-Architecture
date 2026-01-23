package edu.ub.pis2425.projecte.features.usecases._implementation.routines;

import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.routines.DuplicateRoutineUseCase;
import io.reactivex.rxjava3.core.Observable;

public class DuplicateRoutineUseCaseImpl implements DuplicateRoutineUseCase {
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final ClientRepository clientRepository;
    private final IDataService dataService;

    public DuplicateRoutineUseCaseImpl(RoutineRepository routineRepository, ClientRepository clientRepository, IDataService dataService, ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.clientRepository = clientRepository;
        this.dataService = dataService;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Observable<Routine> execute(String id) {
        return routineRepository
                .duplicate(new RoutineId(id))
                .flatMap(duplicatedRoutine ->
                        // Añadir la rutina al cliente
                        clientRepository.addRoutine(dataService.getClientId(), duplicatedRoutine.getId().toString())
                                .andThen(
                                        // Duplicar y añadir ejercicios al cliente
                                        Observable.fromIterable(duplicatedRoutine.getExercises())
                                                .concatMap(originalExercise ->
                                                        exerciseRepository.duplicate(originalExercise.getId())
                                                                .flatMap(duplicatedExercise ->
                                                                        clientRepository.addExercise(dataService.getClientId(), duplicatedExercise.getId())
                                                                                .andThen(Observable.just(duplicatedExercise))
                                                                )
                                                )
                                                .toList()
                                                .toObservable()
                                                .map(duplicatedExercises -> {
                                                    // Asignar los ejercicios duplicados a la rutina
                                                    duplicatedRoutine.setExercises(duplicatedExercises);
                                                    return duplicatedRoutine;
                                                })
                                )
                );
    }

}