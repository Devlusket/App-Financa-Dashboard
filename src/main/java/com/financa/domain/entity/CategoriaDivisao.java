package com.financa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/** Associa explicitamente uma pessoa ao respectivo percentual de uma categoria. */
@Entity
@Table(name = "categoria_divisao", uniqueConstraints = @UniqueConstraint(
        name = "uk_categoria_divisao_categoria_pessoa",
        columnNames = {"categoria_id", "pessoa_id"}
))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoriaDivisao {

    public CategoriaDivisao(Categoria categoria, Pessoa pessoa, BigDecimal percentual) {
        this.categoria = categoria;
        this.pessoa = pessoa;
        this.percentual = percentual;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentual;
}
