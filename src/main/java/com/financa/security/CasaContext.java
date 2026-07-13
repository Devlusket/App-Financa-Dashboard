package com.financa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/** Obtém a casa autenticada para filtrar dados em todos os serviços da API. */
@Component
public class CasaContext {

    public UUID getCasaId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID casaId)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Autenticação obrigatória");
        }
        return casaId;
    }
}
