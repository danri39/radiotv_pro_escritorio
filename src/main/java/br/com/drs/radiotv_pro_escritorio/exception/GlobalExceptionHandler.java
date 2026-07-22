package br.com.drs.radiotv_pro_escritorio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura suas exceções customizadas de "Não Encontrado"
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleNotFound(EntidadeNaoEncontradaException ex) {
        ErroResponse response = new ErroResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), "Não Encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 2. Captura suas exceções customizadas de Regra de Negócio
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraNegocio(RegraNegocioException ex) {
        ErroResponse response = new ErroResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), "Regra de Negócio");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 3. Captura erros de validação do @Valid no DTO (Ex: campo obrigatório faltando)
    // Isso é CRUCIAL para o Front-End saber qual campo está errado
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Retorna algo como: { "nome": "não pode ser vazio", "valor": "deve ser maior que zero" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // 4. Captura TODAS as RuntimeExceptions (incluindo os "throw new RuntimeException" que usamos)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> handleRuntimeException(RuntimeException ex) {
        ErroResponse response = new ErroResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro Interno");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 5. Captura qualquer outra exceção inesperada do sistema (Fallback final)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenericException(Exception ex) {
        ErroResponse response = new ErroResponse(
                "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Inesperado"
        );
        // Opcional: log.error("Erro inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}