package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;

@Mapper(componentModel = "default")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Named("clientFirestoreDtoToClientWithoutExercises")
    static Client clientFirestoreDtoToClientWithoutExercises(ClientFirestoreDto dto) {
        return new Client(
                new ClientId(dto.getId()),
                dto.getPassword(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>()
        );
    }

    @Named("exerciseToId")
    static List<String> exerciseToId(List<Exercise> exercises) {
        if (exercises == null) return new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Exercise exercise : exercises) {
            ids.add(exercise.getId().toString());
        }
        return ids;
    }

    @Named("routineToId")
    static List<String> routineToId(List<Routine> routines) {
        if (routines == null) return new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Routine routine : routines) {
            ids.add(routine.getId().toString());
        }
        return ids;
    }

    @Named("weekToId")
    static List<String> weekToId(List<Week> weeks) {
        if (weeks == null) return new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Week week : weeks) {
            ids.add(week.getId().toString());
        }
        return ids;
    }

    @Named("dayRoutineMapToStringMap")
    static Map<String, List<String>> dayRoutineMapToStringMap(HashMap<Day, List<Routine>> dayRoutineMap) {
        if (dayRoutineMap == null) return new HashMap<>();
        Map<String, List<String>> stringMap = new HashMap<>();
        for (Map.Entry<Day, List<Routine>> entry : dayRoutineMap.entrySet()) {
            List<String> routineIds = new ArrayList<>();
            for (Routine routine : entry.getValue()) {
                routineIds.add(routine.getId().toString());
            }
            stringMap.put(entry.getKey().getId().toString(), routineIds);
        }
        return stringMap;
    }

    @Mapping(target = "id", expression = "java(client.getId().toString())")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "exercises", source = "exercises", qualifiedByName = "exerciseToId")
    @Mapping(target = "routines", source = "routines", qualifiedByName = "routineToId")
    @Mapping(target = "weeks", source = "weeks", qualifiedByName = "weekToId")
    @Mapping(target = "dayRoutineMap", source = "dayRoutineMap", qualifiedByName = "dayRoutineMapToStringMap")
    ClientFirestoreDto clientToClientFirestoreDto(Client client);
}