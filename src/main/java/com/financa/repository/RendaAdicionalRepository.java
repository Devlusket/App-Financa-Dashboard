package com.financa.repository;

import com.financa.domain.entity.RendaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RendaAdicionalRepository extends JpaRepository<RendaAdicional, UUID> {

    Optional<RendaAdicional> findByIdAndRendaPessoaCasaId(UUID id, UUID casaId);
}
