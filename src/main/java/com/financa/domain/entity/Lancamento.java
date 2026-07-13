package com.financa.domain.entity;

import com.financa.domain.enums.StatusLancamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lancamento")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lancamento {

    public Lancamento(
            Casa casa,
            Categoria categoria,
            String descricao,
            BigDecimal valor,
            LocalDate data,
            Pessoa responsavelPagamento,
            StatusLancamento status
    ) {
        this.casa = casa;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.mesReferencia = data.getYear() + "-" + String.format("%02d", data.getMonthValue());
        this.responsavelPagamento = responsavelPagamento;
        this.status = status == null ? StatusLancamento.PENDENTE : status;
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
    private String descricao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_pagamento_id")
    private Pessoa responsavelPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLancamento status = StatusLancamento.PENDENTE;
}
