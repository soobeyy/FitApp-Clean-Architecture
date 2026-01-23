package edu.ub.pis2425.projecte.features.usecases._implementation.week;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;
import edu.ub.pis2425.projecte.features.repositories.WeekRepository;
import edu.ub.pis2425.projecte.features.usecases.week.NavigateWeekUseCase;
import io.reactivex.rxjava3.core.Observable;

public class NavigateWeekUseCaseImpl implements NavigateWeekUseCase {
    private final WeekRepository weekRepository;

    public NavigateWeekUseCaseImpl(WeekRepository weekRepository) {
        this.weekRepository = weekRepository;
    }

    @Override
    public Observable<Week> execute(Week currentWeek, int direction) {
        if (currentWeek == null) {
            return Observable.error(new Throwable("Semana actual no válida"));
        }

        // Calcular la fecha de inicio y fin de la nueva semana
        LocalDate newStartDate = currentWeek.getStartDate().plusDays(direction * 7);
        LocalDate newEndDate = newStartDate.plusDays(6); // La semana termina 6 días después
        // Generar el ID en el formato "YYYY-MM-DD -> YYYY-MM-DD"
        String weekIdString = newStartDate.toString() + " | " + newEndDate.toString();
        WeekId newWeekId = new WeekId(weekIdString);

        return weekRepository.getById(newWeekId)
                .flatMap(week -> Observable.just(week))
                .onErrorResumeNext(throwable -> handleWeekNotFound(newStartDate, newEndDate, direction));
    }

    private Observable<Week> handleWeekNotFound(LocalDate newStartDate, LocalDate newEndDate, int direction) {
        if (direction == -1) {
            return Observable.error(new Throwable("No hay semanas anteriores disponibles para mostrar"));
        } else if (direction == 1) {
            // Crear una nueva semana con el formato correcto
            Week newWeek = Week.create(newStartDate); // Asumo que Week.create genera los días
            return weekRepository.add(newWeek)
                    .andThen(Observable.just(newWeek));
        } else {
            return Observable.error(new Throwable("Dirección de navegación no válida"));
        }
    }
}
