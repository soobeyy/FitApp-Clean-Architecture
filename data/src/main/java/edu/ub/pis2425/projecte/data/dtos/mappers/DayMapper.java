package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import edu.ub.pis2425.projecte.data.dtos.firestore.DayFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;

@Mapper(componentModel = "default")
public interface DayMapper {
    DayMapper INSTANCE = Mappers.getMapper(DayMapper.class);
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Named("dayToDayFirestoreDto")
    static DayFirestoreDto dayToDayFirestoreDto(Day day){
        return new DayFirestoreDto(
                day.getId().toString(),
                day.getDate().format(DATE_FORMATTER)
        );
    }

    @Named("dayFirestoreDtoToDay")
    static Day dayFirestoreDtoToDay(DayFirestoreDto dto){
        return new Day(
                new DayId(dto.getId()),
                LocalDate.parse(dto.getDate(), DATE_FORMATTER)
        );
    }
}