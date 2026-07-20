package com.gymcrm.workload.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_workloads")
@CompoundIndexes({
        @CompoundIndex(name = "first_last_name_idx", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
})
public class TrainerWorkload {
    @Id
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;
    private List<YearSummary> yearsList = new ArrayList<>();

    public TrainerWorkload() {
    }

    public TrainerWorkload(String trainerUsername, String trainerFirstName, String trainerLastName, boolean active) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.active = active;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<YearSummary> getYearsList() {
        return yearsList;
    }

    public void setYearsList(List<YearSummary> yearsList) {
        this.yearsList = yearsList;
    }
}
