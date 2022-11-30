package nl.abnamro.assessment.core.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditingUUIDModel extends UUIDModel {
    @Version
    private Integer version;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt = LocalDateTime.now();

    @LastModifiedBy
    private String lastModifiedBy;
}
