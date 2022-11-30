package nl.abnamro.assessment.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nl.abnamro.assessment.core.model.SoftDeleteModel;
import nl.abnamro.assessment.core.util.RequestContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SoftDeleteRepositoryImpl<T extends SoftDeleteModel> implements SoftDeleteRepository<T> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void softDelete(final T entity) {
        final var userId = RequestContext.userId();
        final var now = LocalDateTime.now();

        entity.setDeletedAt(now);
        entity.setDeletedBy(userId);

        entityManager.persist(entity);
    }
}
