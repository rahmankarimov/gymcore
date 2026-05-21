package com.gymcrm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    private LocalDate trainingDate;
    private int trainingDuration;

    public Training() {
    }

    public Training(Long id, Long traineeId, Long trainerId, String trainingName, String trainingType,
                    LocalDate trainingDate, int trainingDuration) {
        this.id = id;
        setTraineeId(traineeId);
        setTrainerId(trainerId);
        this.trainingName = trainingName;
        setTrainingType(trainingType);
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTraineeId() {
        return trainee == null ? null : trainee.getId();
    }

    public void setTraineeId(Long traineeId) {
        if (traineeId == null) {
            this.trainee = null;
        } else {
            Trainee value = new Trainee();
            value.setId(traineeId);
            this.trainee = value;
        }
    }

    public Long getTrainerId() {
        return trainer == null ? null : trainer.getId();
    }

    public void setTrainerId(Long trainerId) {
        if (trainerId == null) {
            this.trainer = null;
        } else {
            Trainer value = new Trainer();
            value.setId(trainerId);
            this.trainer = value;
        }
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingType() {
        return trainingType == null ? null : trainingType.getTrainingTypeName();
    }

    public void setTrainingType(String trainingType) {
        if (trainingType == null) {
            this.trainingType = null;
        } else {
            TrainingType value = new TrainingType();
            value.setTrainingTypeName(trainingType);
            this.trainingType = value;
        }
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public TrainingType getTrainingTypeEntity() {
        return trainingType;
    }

    public void setTrainingTypeEntity(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public String toString() {
        return "Training{id=" + id + ", trainingName='" + trainingName + "'}";
    }
}
