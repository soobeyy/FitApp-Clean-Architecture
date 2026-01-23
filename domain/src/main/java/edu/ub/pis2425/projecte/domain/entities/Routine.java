package edu.ub.pis2425.projecte.domain.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;

public class Routine implements Serializable {
    private RoutineId id;
    private final String name;
    private final String description;
    private final List<Exercise> exercises;

    public Routine(RoutineId id, String name, String description, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        this.exercises = exercises;
    }

    public static Routine create(String name, String description, List<Exercise> exercises) {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        return new Routine(new RoutineId(), name, description, exercises);
    }

    public RoutineId getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises.clear();
        this.exercises.addAll(exercises);
    }
    public void setId(RoutineId id) {
        this.id = id;
    }

       public static Routine createRoutine(String name, String description, List<Exercise> exercises) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return new Routine(new RoutineId(""), name, description, exercises);
    }

    public static List<String> getIdsFromRoutines(List<Routine> routines) {
        List<String> ids = new ArrayList<>();
        for (Routine routine : routines) {
            ids.add(routine.getId().toString());
        }
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return id != null && id.equals(routine.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}