package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.usecases.week.GetDoneExercisesUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetDoneExercisesUseCaseImpl implements GetDoneExercisesUseCase {

    private final RoutineRepository routineRepository;

    public GetDoneExercisesUseCaseImpl(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Override
    public Observable<List<String>> execute(String routineId, String dayId) {
        return routineRepository.getById(new RoutineId(routineId))
                .map(routine -> routine.getExercises().stream()
                        .filter(exercise -> exercise.getValoracion() != null && exercise.getValoracion().containsKey(dayId))
                        .map(exercise -> exercise.getId().toString())
                        .collect(Collectors.toList()));
    }
}