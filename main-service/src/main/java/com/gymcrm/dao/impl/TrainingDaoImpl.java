package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.domain.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import com.gymcrm.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TrainingDaoImpl implements TrainingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {
        training.setTrainee(entityManager.getReference(Trainee.class, training.getTraineeId()));
        training.setTrainer(entityManager.getReference(Trainer.class, training.getTrainerId()));
        training.setTrainingTypeEntity(findType(training.getTrainingType()));
        entityManager.persist(training);
        LOGGER.info("Created training with name {}", training.getTrainingName());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    public void delete(Long id) {
        findById(id).ifPresent(entityManager::remove);
        LOGGER.info("Deleted training with id {}", id);
    }

    @Override
    public List<Training> findByTraineeCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                                String trainerName, String trainingType) {
        String jpql = """
                select trn from Training trn
                join fetch trn.trainee trainee
                join fetch trn.trainer trainer
                join fetch trn.trainingType type
                where trainee.username = :traineeUsername
                and (:fromDate is null or trn.trainingDate >= :fromDate)
                and (:toDate is null or trn.trainingDate <= :toDate)
                and (:trainerName is null or concat(trainer.firstName, ' ', trainer.lastName) = :trainerName)
                and (:trainingType is null or type.trainingTypeName = :trainingType)
                """;
        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);
        return query.getResultList();
    }

    @Override
    public List<Training> findByTrainerCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                                String traineeName) {
        String jpql = """
                select trn from Training trn
                join fetch trn.trainee trainee
                join fetch trn.trainer trainer
                join fetch trn.trainingType type
                where trainer.username = :trainerUsername
                and (:fromDate is null or trn.trainingDate >= :fromDate)
                and (:toDate is null or trn.trainingDate <= :toDate)
                and (:traineeName is null or concat(trainee.firstName, ' ', trainee.lastName) = :traineeName)
                """;
        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("traineeName", traineeName);
        return query.getResultList();
    }

    @Override
    public List<Training> findAll() {
        return entityManager.createQuery(
                "select t from Training t join fetch t.trainingType", Training.class).getResultList();
    }

    @Override
    public List<TrainingType> findTrainingTypes() {
        return entityManager.createQuery("select t from TrainingType t", TrainingType.class).getResultList();
    }

    private TrainingType findType(String name) {
        return entityManager.createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + name));
    }
}
