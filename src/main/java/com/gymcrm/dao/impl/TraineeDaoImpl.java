package com.gymcrm.dao.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDaoImpl.class);

    private InMemoryStorage storage;

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee.getId() == null) {
            trainee.setId(storage.nextTraineeId());
        } else {
            storage.syncTraineeId(trainee.getId());
        }
        storage.getTrainees().put(trainee.getId(), trainee);
        LOGGER.info("Created trainee with id {}", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        storage.getTrainees().put(trainee.getId(), trainee);
        storage.syncTraineeId(trainee.getId());
        LOGGER.info("Updated trainee with id {}", trainee.getId());
        return trainee;
    }

    @Override
    public void delete(Long id) {
        storage.getTrainees().remove(id);
        LOGGER.info("Deleted trainee with id {}", id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        LOGGER.info("Selecting trainee with id {}", id);
        return Optional.ofNullable(storage.getTrainees().get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.getTrainees().values());
    }

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }
}
