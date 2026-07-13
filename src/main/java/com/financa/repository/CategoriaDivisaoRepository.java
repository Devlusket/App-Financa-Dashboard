package com.financa.repository;

import com.financa.domain.entity.CategoriaDivisao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaDivisaoRepository extends JpaRepository<CategoriaDivisao, UUID> {

    boolean existsByPessoaId(UUID pessoaId);
}
