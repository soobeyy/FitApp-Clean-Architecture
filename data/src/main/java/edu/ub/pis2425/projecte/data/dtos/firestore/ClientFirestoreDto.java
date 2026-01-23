package edu.ub.pis2425.projecte.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

import java.util.List;
import java.util.Map;

public class ClientFirestoreDto {
    @DocumentId
    private String id;
    private String password;
    private List<String> exercises;
    private List<String> routines;
    private List<String> weeks;
    private Map<String, List<String>> dayRoutineMap;

    public ClientFirestoreDto() {}

    public ClientFirestoreDto(String id, String password, List<String> exercises, List<String> routines, List<String> weeks, Map<String, List<String>> dayRoutineMap) {
        this.id = id;
        this.password = password;
        this.exercises = exercises;
        this.routines = routines;
        this.weeks = weeks;
        this.dayRoutineMap = dayRoutineMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }

    public List<String> getRoutines() {
        return routines;
    }

    public void setRoutines(List<String> routines) {
        this.routines = routines;
    }

    public List<String> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<String> weeks) {
        this.weeks = weeks;
    }

    public Map<String, List<String>> getDayRoutineMap() {
        return dayRoutineMap;
    }

    public void setDayRoutineMap(Map<String, List<String>> dayRoutineMap) {
        this.dayRoutineMap = dayRoutineMap;
    }
}