package com.example.turnos_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Validación de Negocio");
        response.put("message", ex.getMessage()); // Acá viaja tu mensaje de "El paciente ya tiene un turno..."

        // Devolvemos 400 (Bad Request) porque es un error de lógica del cliente
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}