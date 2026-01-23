package edu.ub.pis2425.projecte.features.usecases.week;

import edu.ub.pis2425.projecte.domain.entities.Week;
import io.reactivex.rxjava3.core.Observable;

public interface NavigateWeekUseCase {
    /**
     * Navega a la semana anterior o siguiente según la dirección indicada.
     * Crea la semana siguiente si no existe.
     *
     * @param currentWeek semana actual
     * @param direction -1 para anterior, +1 para siguiente
     * @return Observable con la nueva semana
     */
    Observable<Week> execute(Week currentWeek, int direction);
}
