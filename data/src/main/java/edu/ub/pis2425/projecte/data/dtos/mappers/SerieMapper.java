package edu.ub.pis2425.projecte.data.dtos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import edu.ub.pis2425.projecte.data.dtos.firestore.SerieFirestoreDto;
import edu.ub.pis2425.projecte.domain.entities.Sensation;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.SerieId;

@Mapper(componentModel = "default")
public interface SerieMapper {
    SerieMapper INSTANCE = Mappers.getMapper(SerieMapper.class);

    @Named("serieToSerieFirestoreDto")
    static Serie serieToSerieFirestoreDto(SerieFirestoreDto dto) {
        return new Serie(
                new SerieId(dto.getId()),
                (int)dto.getWeight(),
                (int)dto.getRepetitions(),
                stringToEnum(dto.getSensation())
        );
    }

    @Named("stringToEnum")
    static Sensation stringToEnum(String sensation) {
        switch (sensation){
            case "SOBRADO":
                return Sensation.SOBRADO;
            case "NORMAL":
                return Sensation.NORMAL;
            case "JUSTITO":
                return Sensation.JUSTITO;
            default:
                throw new IllegalArgumentException("Invalid sensation: " + sensation);
        }
    }

    @Named("EnumToString")
    static String EnumToString(Sensation sensation) {
        switch (sensation){
            case SOBRADO:
                return "SOBRADO";
            case NORMAL:
                return "NORMAL";
                case JUSTITO:
                return "JUSTITO";
            default:
                throw new IllegalArgumentException("Invalid sensation: " + sensation);
        }
    }


    @Mapping(target = "id", expression = "java(serie.getId().toString())")
    @Mapping(target = "sensation", source = "sensation", qualifiedByName = "EnumToString")
    SerieFirestoreDto serieToSerieFirestoreDto(Serie serie);
}
