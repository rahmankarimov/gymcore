package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.gymcrm.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TrainerDaoImpl implements TrainerDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        entityManager.persist(trainer);
        LOGGER.info("Created trainer with username {}", trainer.getUsername());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        LOGGER.info("Updated trainer with username {}", trainer.getUsername());
        return entityManager.merge(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return entityManager.createQuery("select t from Trainer t where t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean credentialsMatch(String username, String password) {
        Long count = entityManager.createQuery("""
                        select count(t) from Trainer t
                        where t.username = :username and t.password = :password
                        """, Long.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void changePassword(String username, String password) {
        Trainer trainer = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        trainer.setPassword(password);
        LOGGER.info("Changed trainer password for username {}", username);
    }

    @Override
    public void setActive(String username, boolean active) {
        Trainer trainer = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        if (trainer.isActive() == active) {
            throw new IllegalStateException("Trainer active state is already " + active);
        }
        trainer.setActive(active);
        LOGGER.info("Changed trainer active state for username {}", username);
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("select t from Trainer t", Trainer.class).getResultList();
    }
}
