package com.financa.repository;

import com.financa.domain.entity.Renda;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface RendaRepository extends JpaRepository<Renda, UUID> {

    boolean existsByPessoaId(UUID pessoaId);

    @EntityGraph(attributePaths = "adicionais")
    Optional<Renda> findByPessoaIdAndMesReferencia(UUID pessoaId, String mesReferencia);

    Optional<Renda> findTopByPessoaIdAndMesReferenciaLessThanOrderByMesReferenciaDesc(UUID pessoaId, String mesReferencia);

    @EntityGraph(attributePaths = "adicionais")
    Optional<Renda> findByIdAndPessoaCasaId(UUID id, UUID casaId);

    @EntityGraph(attributePaths = {"pessoa", "adicionais"})
    List<Renda> findAllByPessoaCasaIdAndMesReferencia(UUID casaId, String mesReferencia);
}
