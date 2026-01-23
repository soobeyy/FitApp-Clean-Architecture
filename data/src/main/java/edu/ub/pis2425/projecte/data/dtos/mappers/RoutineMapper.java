package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.data.dtos.firestore.RoutineFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;

@Mapper(componentModel = "default")
public interface RoutineMapper {
    RoutineMapper INSTANCE = Mappers.getMapper(RoutineMapper.class);

    @Named("routineFirestoreDtoToRoutineWithoutExercises")
    static Routine routineFirestoreDtoToRoutineWithoutExercises(RoutineFirestoreDto dto) {
        return new Routine(
                new RoutineId(dto.getId()),
                dto.getName(),
                dto.getDescription(),
                new ArrayList<>() // Lista vacía de ejercicios
        );
    }

    @Named("exerciseToId")
    static List<String> exerciseToId(List<Exercise> exercises) {
        if (exercises == null) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>();
        for (Exercise exercise : exercises) {
            ids.add(exercise.getId().toString());
        }
        return ids;
    }

    @Mapping(target = "id", expression = "java(routine.getId().toString())")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "exercises", source = "exercises", qualifiedByName = "exerciseToId")
    RoutineFirestoreDto routineToRoutineFirestoreDto(Routine routine);
}
