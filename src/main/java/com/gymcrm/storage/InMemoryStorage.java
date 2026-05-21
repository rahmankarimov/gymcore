package com.gymcrm.storage;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.domain.TrainingType;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    private final Map<Long, Trainee> trainees = new LinkedHashMap<>();
    private final Map<Long, Trainer> trainers = new LinkedHashMap<>();
    private final Map<Long, Training> trainings = new LinkedHashMap<>();
    private final Map<Long, TrainingType> trainingTypes = new LinkedHashMap<>();

    private final AtomicLong traineeIdSequence = new AtomicLong(0);
    private final AtomicLong trainerIdSequence = new AtomicLong(0);
    private final AtomicLong trainingIdSequence = new AtomicLong(0);
    private final AtomicLong trainingTypeIdSequence = new AtomicLong(0);
    private JdbcTemplate jdbcTemplate;

    public Map<Long, Trainee> getTrainees() {
        return trainees;
    }

    public Map<Long, Trainer> getTrainers() {
        return trainers;
    }

    public Map<Long, Training> getTrainings() {
        return trainings;
    }

    public Map<Long, TrainingType> getTrainingTypes() {
        return trainingTypes;
    }

    public long nextTraineeId() {
        return traineeIdSequence.incrementAndGet();
    }

    public long nextTrainerId() {
        return trainerIdSequence.incrementAndGet();
    }

    public long nextTrainingId() {
        return trainingIdSequence.incrementAndGet();
    }

    public long nextTrainingTypeId() {
        return trainingTypeIdSequence.incrementAndGet();
    }

    public void syncTraineeId(long id) {
        syncSequence(traineeIdSequence, id);
    }

    public void syncTrainerId(long id) {
        syncSequence(trainerIdSequence, id);
    }

    public void syncTrainingId(long id) {
        syncSequence(trainingIdSequence, id);
    }

    public void syncTrainingTypeId(long id) {
        syncSequence(trainingTypeIdSequence, id);
    }

    public TrainingType findOrCreateTrainingType(String name) {
        Optional<TrainingType> existingType = trainingTypes.values().stream()
                .filter(type -> type.getTrainingTypeName().equalsIgnoreCase(name))
                .findFirst();
        if (existingType.isPresent()) {
            return existingType.get();
        }

        long id = nextTrainingTypeId();
        TrainingType trainingType = new TrainingType(id, name);
        trainingTypes.put(id, trainingType);
        return trainingType;
    }

    public void clear() {
        trainees.clear();
        trainers.clear();
        trainings.clear();
        trainingTypes.clear();
        traineeIdSequence.set(0);
        trainerIdSequence.set(0);
        trainingIdSequence.set(0);
        trainingTypeIdSequence.set(0);
        clearDatabase();
    }

    private void clearDatabase() {
        if (jdbcTemplate == null) {
            return;
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        List.of("trainings", "trainee_trainers", "trainees", "trainers", "users")
                .forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY"));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Autowired(required = false)
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void syncSequence(AtomicLong sequence, long id) {
        sequence.updateAndGet(current -> Math.max(current, id));
    }
}
