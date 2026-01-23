package edu.ub.pis2425.projecte.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class SerieId implements Serializable {

    private String id;

    public SerieId(String id) { this.id = id; }

    public SerieId() { this.id = ""; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SerieId serieId = (SerieId) obj;
        return Objects.equals(id, serieId.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return id;
    }
}
