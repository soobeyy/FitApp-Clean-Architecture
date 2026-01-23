package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;

public class DTOToDomainMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // No need to define setters
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        /* Conversió entre tipus no coincidents */
        modelMapper.addConverter(new AbstractConverter<String, ClientId>() {
            @Override
            protected ClientId convert(String source) {
                return source == null ? null : new ClientId(source);
            }
        });
        modelMapper.addConverter(new AbstractConverter<String, ExerciseId>() {
            @Override
            protected ExerciseId convert(String source) {
                return source == null ? null : new ExerciseId(source);
            }
        });
        modelMapper.addConverter(new AbstractConverter<ClientId, String>() {
            @Override
            protected String convert(ClientId source) {
                return source == null ? null : source.toString();
            }
        });
        modelMapper.addConverter(new AbstractConverter<ExerciseId, String>() {
            @Override
            protected String convert(ExerciseId source) {
                return source == null ? null : source.toString();
            }
        });
        modelMapper.addConverter(new AbstractConverter<String, WeekId>() {
            @Override
            protected WeekId convert(String source) {
                return source == null ? null : new WeekId(source);
            }
        });
        modelMapper.addConverter(new AbstractConverter<WeekId, String>() {
            @Override
            protected String convert(WeekId source) {
                return source == null ? null : source.toString();
            }
        });
        modelMapper.addConverter(new AbstractConverter<String, DayId>() {
            @Override
            protected DayId convert(String source) {
                return source == null ? null : new DayId(source);
            }
        });
        modelMapper.addConverter(new AbstractConverter<DayId, String>() {
            @Override
            protected String convert(DayId source) {
                return source == null ? null : source.toString();
            }
        });
    }

    public static <T> T map(Object source, Class<T> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
