package com.financa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "conta_fixa")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContaFixa {

    public ContaFixa(Casa casa, Categoria categoria, String nome, BigDecimal valorAtual) {
        this.casa = casa;
        this.categoria = categoria;
        this.nome = nome;
        this.valorAtual = valorAtual;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "casa_id", nullable = false)
    private Casa casa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String nome;

    @Column(name = "valor_atual", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorAtual;
}
