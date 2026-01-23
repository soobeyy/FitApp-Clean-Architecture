package edu.ub.pis2425.projecte.data.dtos.firestore;

import java.util.List;

public class WeekFirestoreDto {
    private String id;
    private String startDate; // Formato: "YYYY-MM-DD", e.g., "2025-05-11"
    private String endDate;  // Formato: "YYYY-MM-DD", e.g., "2025-05-17"
    private List<String> days;

    public WeekFirestoreDto() {}

    public WeekFirestoreDto(String id, String startDate, String endDate, List<String> days) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}