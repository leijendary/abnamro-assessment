package nl.abnamro.assessment.core.repository;

import nl.abnamro.assessment.core.model.SoftDeleteModel;

public interface SoftDeleteRepository<T extends SoftDeleteModel> {
    void softDelete(T t);
}
