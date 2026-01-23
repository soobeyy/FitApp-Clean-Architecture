package edu.ub.pis2425.projecte.domain.entities;

import java.io.Serializable;

import edu.ub.pis2425.projecte.domain.valueobjects.SerieId;

public class Serie implements Serializable {

    private SerieId id;
    private int weight;
    private int repetitions;
    private Sensation sensation;

    public Serie(SerieId id, int weight, int repetitions, Sensation sensation) {
        this.id = id;
        this.weight = weight;
        this.repetitions = repetitions;
        this.sensation = sensation;
    }

    //Getters y setters
    public void setId(SerieId id) { this.id = id; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public void setSensation(Sensation sensation) { this.sensation = sensation; }

    public SerieId getId() { return id; }
    public int getWeight() { return weight; }
    public int getRepetitions() { return repetitions; }
    public Sensation getSensation() { return sensation; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Serie serie = (Serie) o;
        return id != null && id.equals(serie.id);
    }

    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static Serie createSerie(int weight, int repetitions, Sensation sensation){
        if(weight < 0) throw new IllegalArgumentException("Weight must be greater than 0");
        if(repetitions < 0) throw new IllegalArgumentException("Repetitions must be greater than 0");
        if(sensation == null) throw new IllegalArgumentException("Sensation cannot be null");
        return new Serie(new SerieId(""), weight, repetitions, sensation);
    }

}
