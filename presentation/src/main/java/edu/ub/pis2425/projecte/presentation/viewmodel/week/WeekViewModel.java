package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.features.usecases.week.GetRoutinesFromDayUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.NavigateWeekUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeekViewModel extends ViewModel {
    private final MutableLiveData<List<Day>> weekDays = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<HashMap<Day, List<Routine>>> dayRoutineMap = new MutableLiveData<>();
    private final GetRoutinesFromDayUseCase getRoutinesFromDayUseCase;
    private final NavigateWeekUseCase navigateWeekUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Week> currentWeek = new MutableLiveData<>(); // Changed to MutableLiveData

    public WeekViewModel(GetRoutinesFromDayUseCase getRoutinesFromDayUseCase, NavigateWeekUseCase navigateWeekUseCase) {
        this.getRoutinesFromDayUseCase = getRoutinesFromDayUseCase;
        this.navigateWeekUseCase = navigateWeekUseCase;
        // Initialize with the current week based on today's date
        currentWeek.setValue(Week.create(LocalDate.now()));
        loadWeek(currentWeek.getValue()); // Load the initial week
    }

    public LiveData<List<Day>> getWeekDays() {
        return weekDays;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<HashMap<Day, List<Routine>>> getDayRoutineMap() {
        return dayRoutineMap;
    }

    public LiveData<Week> getCurrentWeek() { // Updated return type to LiveData<Week>
        return currentWeek;
    }

    public void loadWeek(Week week) {
        if (week == null) {
            errorMessage.setValue("Semana no válida");
            return;
        }
        this.currentWeek.setValue(week); // Update LiveData
        weekDays.setValue(week.getDays()); // Load the days of the week
        loadRoutinesForDays(week.getDays()); // Load routines for the days
    }

    private void loadRoutinesForDays(List<Day> days) {
        // Create a list of Observables, one for each day
        List<Observable<Pair<Day, List<Routine>>>> routineObservables = days.stream()
                .map(day -> getRoutinesFromDayUseCase.execute(day.getId().toString())
                        .map(routines -> new Pair<>(day, routines))
                        .onErrorReturn(throwable -> new Pair<>(day, new ArrayList<>())))
                .collect(Collectors.toList());

        if (routineObservables.isEmpty()) {
            dayRoutineMap.setValue(new HashMap<>());
            return;
        }

        // Combine all Observables into a single HashMap
        disposables.add(
                Observable.combineLatest(routineObservables, results -> {
                            HashMap<Day, List<Routine>> map = new HashMap<>();
                            for (Object result : results) {
                                Pair<Day, List<Routine>> pair = (Pair<Day, List<Routine>>) result;
                                map.put(pair.first, pair.second);
                            }
                            return map;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                map -> dayRoutineMap.setValue(map),
                                throwable -> dayRoutineMap.setValue(new HashMap<>())
                        )
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

    // Helper class to combine Day and List<Routine>
    private static class Pair<T, U> {
        final T first;
        final U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    // Method to navigate week (direction -1 or 1)
    public void navigateWeek(int direction) {
        if (direction != -1 && direction != 1) {
            errorMessage.setValue("Dirección de navegación no válida");
            return;
        }
        Week current = currentWeek.getValue(); // Access LiveData value
        if (current == null) {
            errorMessage.setValue("No hay semana actual cargada");
            return;
        }

        disposables.add(
                navigateWeekUseCase.execute(current, direction)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                newWeek -> loadWeek(newWeek), // Update via loadWeek to set LiveData
                                error -> errorMessage.setValue(error.getMessage())
                        )
        );
    }
}