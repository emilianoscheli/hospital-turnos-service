package com.example.turnos_service.domain.exception;

public class TurnoNoEncontradoException extends RuntimeException {
    public TurnoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}