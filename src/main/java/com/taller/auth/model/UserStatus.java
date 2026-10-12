package com.taller.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "status_usuario")
public class UserStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "str_valor", nullable = false, unique = true, length = 20)
    private String value;

    @Column(name = "str_descripcion", nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Instant actualizado;

    @Column(nullable = false)
    private boolean deleted;

    public Long getId() { return id; }
    public String getValue() { return value; }
    public String getDescription() { return description; }
    public Instant getActualizado() { return actualizado; }
    public boolean isDeleted() { return deleted; }
    public void setValue(String value) { this.value = value; }
    public void setDescription(String description) { this.description = description; }
    public void setActualizado(Instant actualizado) { this.actualizado = actualizado; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    @PreUpdate public void touch() { actualizado = Instant.now(); }
}
