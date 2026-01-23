package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.data.dtos.firestore.WeekFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;

@Mapper(componentModel = "default")
public interface WeekMapper {
    WeekMapper INSTANCE = Mappers.getMapper(WeekMapper.class);

    // Formato estándar para las fechas
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // "YYYY-MM-DD"

    @Named("weekFirestoreDtoToWeekWithoutDays")
    static Week weekFirestoreDtoToWeekWithoutDays(WeekFirestoreDto dto) {
        return new Week(
                new WeekId(dto.getId()),
                LocalDate.parse(dto.getStartDate(), DATE_FORMATTER),
                LocalDate.parse(dto.getEndDate(), DATE_FORMATTER),
                new ArrayList<>()
        );
    }

    @Named("weekToWeekFirestoreDto")
    static WeekFirestoreDto weekToWeekFirestoreDto(Week week) {
        return new WeekFirestoreDto(
                week.getId().toString(),
                week.getStartDate().format(DATE_FORMATTER),
                week.getEndDate().format(DATE_FORMATTER),
                dayToId(week.getDays())
                );
    }

    // Método auxiliar para convertir List<Day> a List<String>
    @Named("dayToId")
    static List<String> dayToId(List<Day> days) {
        if (days == null) return new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Day day : days) {
            ids.add(day.getId().toString());
        }
        return ids;
    }
}