package nl.abnamro.assessment.repository;

import jakarta.persistence.LockModeType;
import nl.abnamro.assessment.core.repository.SoftDeleteRepository;
import nl.abnamro.assessment.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe>,
        SoftDeleteRepository<Recipe> {
    @Lock(LockModeType.WRITE)
    Optional<Recipe> findLockedById(final UUID id);
}
