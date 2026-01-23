package edu.ub.pis2425.projecte.presentation.pos.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;

public class DomainToPOMapper extends ModelMapper{
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // No need to define setters
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        modelMapper.addConverter(new AbstractConverter<ExerciseId, String>() {
            @Override
            protected String convert(ExerciseId source) {
                return source != null ? source.toString() : null;
            }
        });

        modelMapper.addConverter(new AbstractConverter<ExerciseId, String>() {
            @Override
            protected String convert(ExerciseId source) {
                return source != null ? source.toString() : null;
            }
        });
    }

    public static <S, T> T mapObject(S source, Class<T> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
