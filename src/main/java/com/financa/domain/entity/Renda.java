package com.financa.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "renda", uniqueConstraints = @UniqueConstraint(
        name = "uk_renda_pessoa_mes_referencia",
        columnNames = {"pessoa_id", "mes_referencia"}
))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Renda {

    public Renda(Pessoa pessoa, String mesReferencia, BigDecimal valorFixo) {
        this.pessoa = pessoa;
        this.mesReferencia = mesReferencia;
        this.valorFixo = valorFixo;
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "valor_fixo", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorFixo;

    @OneToMany(mappedBy = "renda")
    private List<RendaAdicional> adicionais = new ArrayList<>();
}
