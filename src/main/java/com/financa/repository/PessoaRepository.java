package com.financa.repository;

import com.financa.domain.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    List<Pessoa> findAllByCasaIdOrderByNomeAsc(UUID casaId);

    Optional<Pessoa> findByIdAndCasaId(UUID id, UUID casaId);

    long countByCasaId(UUID casaId);
}
