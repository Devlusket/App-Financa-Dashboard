package com.financa.repository;

import com.financa.domain.entity.Casa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CasaRepository extends JpaRepository<Casa, UUID> {

    Optional<Casa> findByEmail(String email);

    boolean existsByEmail(String email);
}
