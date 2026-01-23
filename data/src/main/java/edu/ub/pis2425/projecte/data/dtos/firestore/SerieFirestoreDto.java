package edu.ub.pis2425.projecte.data.dtos.firestore;

public class SerieFirestoreDto {

    private String id;
    private long weight;
    private long repetitions;
    private String sensation;

    public SerieFirestoreDto() {}

    public SerieFirestoreDto(String id, long weight, long repetitions, String sensation) {
        this.id = id;
        this.weight = weight;
        this.repetitions = repetitions;
        this.sensation = sensation;
    }

    public String getId() { return id; }
    public long getWeight() { return weight; }
    public long getRepetitions() { return repetitions; }
    public String getSensation() { return sensation; }


    public void setId(String id) { this.id = id; }
    public void setWeight(long weight) { this.weight = weight; }
    public void setRepetitions(long repetitions) { this.repetitions = repetitions; }
    public void setSensation(String sensation){this.sensation = sensation;}
}
