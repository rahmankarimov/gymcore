package com.gymcrm.domain;

public class TrainingType {
    private Long id;
    private String trainingTypeName;

    public TrainingType() {
    }

    public TrainingType(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
