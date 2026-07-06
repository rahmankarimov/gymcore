package com.gymcrm.dao.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.gymcrm.exception.EntityNotFoundException;
import com.gymcrm.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public class TraineeDaoImpl implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);
        LOGGER.info("Created trainee with username {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        LOGGER.info("Updated trainee with username {}", trainee.getUsername());
        return entityManager.merge(trainee);
    }

    @Override
    public void delete(Long id) {
        findById(id).ifPresent(entityManager::remove);
        LOGGER.info("Deleted trainee with id {}", id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return entityManager.createQuery("""
                        select distinct t from Trainee t
                        left join fetch t.trainers
                        where t.username = :username
                        """, Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean credentialsMatch(String username, String password) {
        Long count = entityManager.createQuery("""
                        select count(t) from Trainee t
                        where t.username = :username and t.password = :password
                        """, Long.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void changePassword(String username, String password) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        trainee.setPassword(password);
        LOGGER.info("Changed trainee password for username {}", username);
    }

    @Override
    public void setActive(String username, boolean active) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        if (trainee.isActive() == active) {
            throw new IllegalStateException("Trainee active state is already " + active);
        }
        trainee.setActive(active);
        LOGGER.info("Changed trainee active state for username {}", username);
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        entityManager.remove(trainee);
        LOGGER.info("Deleted trainee with username {}", username);
    }

    @Override
    public List<Trainer> findUnassignedTrainers(String username) {
        return entityManager.createQuery("""
                        select distinct tr from Trainer tr
                        where tr not in (
                            select assigned from Trainee t join t.trainers assigned
                            where t.username = :username
                        )
                        """, Trainer.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public Trainee updateTrainers(String username, Set<String> trainerUsernames) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        List<Trainer> trainers = entityManager.createQuery(
                        "select tr from Trainer tr where tr.username in :usernames", Trainer.class)
                .setParameter("usernames", trainerUsernames)
                .getResultList();
        if (trainers.size() != trainerUsernames.size()) {
            throw new EntityNotFoundException("One or more trainers were not found");
        }
        trainee.setTrainers(new HashSet<>(trainers));
        LOGGER.info("Updated trainers list for trainee {}", username);
        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("select t from Trainee t", Trainee.class).getResultList();
    }
}
