package edu.ub.pis2425.projecte.domain.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;

public class Exercise implements Serializable {
    private ExerciseId id;
    private String name;
    private String description;
    private String imageUri; // URI de la imagen (opcional)
    private HashMap<String, List<Serie>> valoracion;

    public Exercise(ExerciseId id, String name, String description, String imageUri, HashMap<String, List<Serie>> valoracion) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
        this.valoracion = valoracion != null ? new HashMap<>(valoracion) : new HashMap<>();
    }

    public Exercise() {
        // Constructor vacío para Firestore
    }

    // Getters y setters
    public void setId(ExerciseId id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public void setValoracion(HashMap<String, List<Serie>> valoracion) {
        this.valoracion = valoracion;
    }

    public ExerciseId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public HashMap<String, List<Serie>> getValoracion() {
        return valoracion;
    }

    public static Exercise createExercise(String name, String description, String imageUri) {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        else if (description.isEmpty())
            throw new IllegalArgumentException("Description cannot be empty");

        return new Exercise(new ExerciseId(""),name, description, imageUri, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id != null && id.equals(exercise.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}