package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface ClientRepository {
    interface OnUpdateListener {
        void update(Client client);
    }

    Completable add(Client client);
    Observable<Client> getById(ClientId id);
    Observable<Boolean> update(ClientId id, OnUpdateListener onUpdateListener);
    Completable addExercise(ClientId clientId, ExerciseId exerciseId);
    Completable removeExercise(ClientId clientId, ExerciseId exerciseId);
    Completable addBasic(ClientId clientId);
    Completable addRoutine(ClientId clientId, String routineId);
    Completable removeRoutine(ClientId clientId, String routineId);
    Completable setRoutinesForDay(ClientId clientId, String dayId, List<String> routineId);
}
