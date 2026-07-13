package com.financa.repository;

import com.financa.domain.entity.Categoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    @EntityGraph(attributePaths = {"responsavel", "divisoesPercentuais", "divisoesPercentuais.pessoa"})
    List<Categoria> findAllByCasaIdOrderByNomeAsc(UUID casaId);

    @EntityGraph(attributePaths = {"responsavel", "divisoesPercentuais", "divisoesPercentuais.pessoa"})
    Optional<Categoria> findByIdAndCasaId(UUID id, UUID casaId);

    boolean existsByResponsavelId(UUID pessoaId);
}
