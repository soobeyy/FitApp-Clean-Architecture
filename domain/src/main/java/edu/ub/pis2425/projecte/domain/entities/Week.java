package edu.ub.pis2425.projecte.domain.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;

public class Week {
    private WeekId id;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Day> days;

    public Week(WeekId id, LocalDate startDate, LocalDate endDate, List<Day> days) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days != null ? days : new ArrayList<>();
    }

    public static Week create(LocalDate registrationDate) {
        LocalDate start = registrationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = registrationDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<Day> days = new ArrayList<>();

        LocalDate currentDay = start;
        while (!currentDay.isAfter(end)) {
            Day day = Day.create(currentDay);
            days.add(day);
            currentDay = currentDay.plusDays(1);
        }

        String id = start.format(DateTimeFormatter.ISO_LOCAL_DATE) + " | " + end.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return new Week(new WeekId(id), start, end, days);
    }

    // Getters
    public WeekId getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<Day> getDays() {
        return days;
    }

    // Setters
    public void setId(WeekId id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setDays(List<Day> days) {
        this.days = days != null ? days : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        return id != null && id.equals(week.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}