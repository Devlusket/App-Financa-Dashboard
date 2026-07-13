package com.financa.repository;

import com.financa.domain.entity.Lancamento;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID>, JpaSpecificationExecutor<Lancamento> {

    boolean existsByCategoriaId(UUID categoriaId);

    boolean existsByResponsavelPagamentoId(UUID pessoaId);

    Optional<Lancamento> findByIdAndCasaId(UUID id, UUID casaId);

    @Override
    @EntityGraph(attributePaths = {"categoria", "responsavelPagamento"})
    List<Lancamento> findAll(Specification<Lancamento> specification, Sort sort);

    @EntityGraph(attributePaths = {
            "categoria",
            "categoria.responsavel",
            "categoria.divisoesPercentuais",
            "categoria.divisoesPercentuais.pessoa"
    })
    List<Lancamento> findAllByCasaIdAndMesReferencia(UUID casaId, String mesReferencia);
}
