package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingDao;
import com.gymcrm.domain.Training;
import com.gymcrm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDaoImpl implements TrainingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDaoImpl.class);

    private InMemoryStorage storage;

    @Override
    public Training save(Training training) {
        if (training.getId() == null) {
            training.setId(storage.nextTrainingId());
        } else {
            storage.syncTrainingId(training.getId());
        }
        storage.getTrainings().put(training.getId(), training);
        storage.findOrCreateTrainingType(training.getTrainingType());
        LOGGER.info("Created training with id {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        LOGGER.info("Selecting training with id {}", id);
        return Optional.ofNullable(storage.getTrainings().get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.getTrainings().values());
    }

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }
}
