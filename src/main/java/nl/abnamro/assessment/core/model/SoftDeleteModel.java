package nl.abnamro.assessment.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface SoftDeleteModel extends Serializable {
    void setDeletedAt(LocalDateTime deletedAt);

    LocalDateTime getDeletedAt();

    void setDeletedBy(String deletedBy);

    String getDeletedBy();
}
