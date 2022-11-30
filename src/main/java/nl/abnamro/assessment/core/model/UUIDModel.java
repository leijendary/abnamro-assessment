package nl.abnamro.assessment.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class UUIDModel implements Serializable {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    private UUID id = UUID.randomUUID();
}
