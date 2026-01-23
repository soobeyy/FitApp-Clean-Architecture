package edu.ub.pis2425.projecte.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class DayId implements Serializable {
    private String id;

    public DayId(String id) {
        this.id = id;
    }

    public DayId() {
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DayId dayId = (DayId) obj;
        return Objects.equals(id, dayId.id);
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