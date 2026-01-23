package edu.ub.pis2425.projecte.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class RoutineId implements Serializable {
    private String id;

    public RoutineId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public RoutineId() {}

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
        RoutineId routineId = (RoutineId) obj;
        return Objects.equals(id, routineId.id);
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
