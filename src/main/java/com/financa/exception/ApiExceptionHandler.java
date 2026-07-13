package com.financa.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> responseStatus(ResponseStatusException exception, HttpServletRequest request) {
        return resposta(exception.getStatusCode().value(), exception.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacao(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String mensagem = exception.getBindingResult().getFieldErrors().stream()
                .map(this::mensagemCampo)
                .collect(Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST.value(), mensagem, request);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> requisicaoInvalida(Exception exception, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST.value(), "Requisição inválida", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> conflito(DataIntegrityViolationException exception, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT.value(), "Operação conflita com dados existentes", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> naoEncontrado(NoResourceFoundException exception, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> inesperado(Exception exception, HttpServletRequest request) {
        log.error("Erro não tratado em {}", request.getRequestURI(), exception);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor", request);
    }

    private String mensagemCampo(FieldError erro) {
        return erro.getField() + ": " + erro.getDefaultMessage();
    }

    private ResponseEntity<ApiError> resposta(int status, String mensagem, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status, httpStatus.getReasonPhrase(), mensagem, request.getRequestURI()
        ));
    }
}
