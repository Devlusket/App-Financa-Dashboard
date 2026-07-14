package com.financa.auth;

import com.financa.auth.dto.AuthResponse;
import com.financa.auth.dto.LoginRequest;
import com.financa.auth.dto.RegistroRequest;
import com.financa.domain.entity.Casa;
import com.financa.repository.CasaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CasaRepository casaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String usuario = request.usuario().trim().toLowerCase();
        if (casaRepository.existsByUsuario(usuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já cadastrado");
        }

        Casa casa = new Casa(
                usuario,
                passwordEncoder.encode(request.senha()),
                request.nome() == null || request.nome().isBlank() ? null : request.nome().trim()
        );

        Casa salva = casaRepository.save(casa);
        return new AuthResponse(jwtService.gerarToken(salva));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Casa casa = casaRepository.findByUsuario(request.usuario().trim().toLowerCase())
                .orElseThrow(() -> credenciaisInvalidas());

        if (!passwordEncoder.matches(request.senha(), casa.getSenhaHash())) {
            throw credenciaisInvalidas();
        }
        return new AuthResponse(jwtService.gerarToken(casa));
    }

    private ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
    }
}
