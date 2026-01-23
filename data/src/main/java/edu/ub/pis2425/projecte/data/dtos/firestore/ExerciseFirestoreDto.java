package edu.ub.pis2425.projecte.data.dtos.firestore;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseFirestoreDto {
    private String id;

    private String name;
    private String description;
    private String image;
    private HashMap<String, List<String>> valoracion;

    public ExerciseFirestoreDto() {}

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setValoracion(HashMap<String, List<String>> valoracion) {
        this.valoracion = valoracion;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getImage() {
        return image;
    }
    public Map<String, List<String>> getValoracion() {
        return valoracion;
    }
}
