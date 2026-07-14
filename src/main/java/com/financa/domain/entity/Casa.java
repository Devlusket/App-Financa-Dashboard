package com.financa.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "casa")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Casa {

    public Casa(String usuario, String senhaHash, String nome) {
        this.usuario = usuario;
        this.senhaHash = senhaHash;
        this.nome = nome;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    private String nome;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "casa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pessoa> pessoas = new ArrayList<>();
}
