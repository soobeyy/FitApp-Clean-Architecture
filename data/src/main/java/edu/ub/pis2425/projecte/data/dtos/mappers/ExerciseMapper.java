package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ub.pis2425.projecte.data.dtos.firestore.ExerciseFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;

@Mapper
public interface ExerciseMapper {

    ExerciseMapper INSTANCE = Mappers.getMapper(ExerciseMapper.class);

    static Exercise exerciseFirestoreDtoToExerciseWithoutValoracion(ExerciseFirestoreDto exerciseDto) {
        return new Exercise(
                new ExerciseId(exerciseDto.getId()),
                exerciseDto.getName(),
                exerciseDto.getDescription(),
                exerciseDto.getImage(),
                new HashMap<>() // valoracion vacía
        );
    }

    @Named("valoracionMapToStringMap")
    static HashMap<String, List<String>> valoracionMapToStringMap(HashMap<String, List<Serie>> valoracion) {
        if (valoracion == null) return new HashMap<>();

        HashMap<String, List<String>> stringMap = new HashMap<>();
        for (HashMap.Entry<String, List<Serie>> entry : valoracion.entrySet()) {
            List<String> serieIds = new ArrayList<>();
            for (Serie serie : entry.getValue()) {
                serieIds.add(serie.getId().toString());
            }
            stringMap.put(entry.getKey(), serieIds);
        }
        return stringMap;
    }

    @Mapping(target = "id", expression = "java(exercise.getId().toString())")
    @Mapping(target = "image", source = "imageUri")
    @Mapping(target = "valoracion", source = "valoracion", qualifiedByName = "valoracionMapToStringMap")
    ExerciseFirestoreDto exerciseToExerciseFirestoreDto(Exercise exercise);
}
