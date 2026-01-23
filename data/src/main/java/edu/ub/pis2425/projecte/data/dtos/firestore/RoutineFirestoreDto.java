package edu.ub.pis2425.projecte.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class RoutineFirestoreDto {
    /* Attributes */
    private String id;
    private String name;
    private String description; // Opcional, puede ser null
    private List<String> exercises;

    /**
     * Empty constructor.
     */
    @SuppressWarnings("unused")
    public RoutineFirestoreDto() {}

    public RoutineFirestoreDto(String id, String name, String description, List<String> exercises) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exercises = exercises;
    }

    /* Getters */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExercises() {
        return exercises;
    }

    /* Setters */
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }
}
