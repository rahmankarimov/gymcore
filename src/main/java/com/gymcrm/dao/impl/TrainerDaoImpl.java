package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoImpl.class);

    private InMemoryStorage storage;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer.getId() == null) {
            trainer.setId(storage.nextTrainerId());
        } else {
            storage.syncTrainerId(trainer.getId());
        }
        storage.getTrainers().put(trainer.getId(), trainer);
        LOGGER.info("Created trainer with id {}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        storage.getTrainers().put(trainer.getId(), trainer);
        storage.syncTrainerId(trainer.getId());
        LOGGER.info("Updated trainer with id {}", trainer.getId());
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        LOGGER.info("Selecting trainer with id {}", id);
        return Optional.ofNullable(storage.getTrainers().get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.getTrainers().values());
    }

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }
}
