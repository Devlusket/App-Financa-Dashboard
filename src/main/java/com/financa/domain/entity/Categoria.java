package com.financa.domain.entity;

import com.financa.domain.enums.TipoDivisao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Categoria {

    public Categoria(Casa casa, String nome, TipoDivisao tipoDivisao, boolean ehPoupanca) {
        this.casa = casa;
        this.nome = nome;
        this.tipoDivisao = tipoDivisao;
        this.ehPoupanca = ehPoupanca;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "casa_id", nullable = false)
    private Casa casa;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_divisao", nullable = false)
    private TipoDivisao tipoDivisao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Pessoa responsavel;

    @Column(name = "eh_poupanca", nullable = false)
    private boolean ehPoupanca = false;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoriaDivisao> divisoesPercentuais = new ArrayList<>();
}
