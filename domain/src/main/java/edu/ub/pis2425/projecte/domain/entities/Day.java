package edu.ub.pis2425.projecte.domain.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import edu.ub.pis2425.projecte.domain.valueobjects.DayId;

public class Day implements Serializable {
    private DayId id;
    private final LocalDate date;

    public Day(DayId id, LocalDate date) {
        this.id = id;
        this.date = date;
    }

    public static Day create(LocalDate date) {
        return new Day(new DayId(date.format(DateTimeFormatter.ISO_LOCAL_DATE)), date);
    }

    public DayId getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setId(DayId id) {
        this.id = id;
    }

    public String getName() {
        return date.getDayOfWeek().toString().toLowerCase().substring(0, 3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return id != null && id.equals(day.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static int compareTo(String date) {
        // 1- El dia pasado es mayor
        // 2- El dia pasado es en el que nos encontramos
        // 3- El dia pasado es menor

        LocalDate currentDate = LocalDate.now();
        LocalDate passedDate = LocalDate.parse(date);

        if (passedDate.isAfter(currentDate)) {
            return 1;
        } else if (passedDate.isEqual(currentDate)) {
            return 2;
        } else {
            return 3;
        }
    }
}