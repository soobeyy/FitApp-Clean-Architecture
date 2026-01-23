package edu.ub.pis2425.projecte.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class ExerciseId implements Serializable {
    private String id;

    public ExerciseId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public ExerciseId() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExerciseId exerciseId = (ExerciseId) obj;
        return Objects.equals(id, exerciseId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
