package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import edu.ub.pis2425.projecte.domain.entities.Sensation;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.SerieRepository;
import edu.ub.pis2425.projecte.features.usecases.week.RateSerieUseCase;
import io.reactivex.rxjava3.core.Completable;

public class RateSerieUseCaseImpl implements RateSerieUseCase {

    private final ExerciseRepository exerciseRepository;
    private final SerieRepository serieRepository;

    public RateSerieUseCaseImpl(ExerciseRepository exerciseRepository, SerieRepository serieRepository) {
        this.exerciseRepository = exerciseRepository;
        this.serieRepository = serieRepository;
    }

    @Override
    public Completable execute(int kg, int reps, Sensation sensation, String dayId, String exerciseId){
        Serie serie = Serie.createSerie(kg, reps, sensation);
        return serieRepository.add(serie)
                .andThen(Completable.defer(() ->
                        exerciseRepository.addSerieToExercise(exerciseId, dayId, serie)));
    }
}
