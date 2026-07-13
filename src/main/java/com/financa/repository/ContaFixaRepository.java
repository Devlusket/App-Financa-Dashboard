package com.financa.repository;

import com.financa.domain.entity.ContaFixa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContaFixaRepository extends JpaRepository<ContaFixa, UUID> {

    boolean existsByCategoriaId(UUID categoriaId);

    List<ContaFixa> findAllByCasaIdOrderByNomeAsc(UUID casaId);

    Optional<ContaFixa> findByIdAndCasaId(UUID id, UUID casaId);
}
