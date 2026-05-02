package com.example.turnos_service.domain.model;

public enum EstadoTurno {
    PENDIENTE,   // Cuando se crea (agregarConsulta)
    CONFIRMADO,  // Cuando llega a la sala de espera (confirmarAsistencia)
    AUSENTE,     // Cuando no se presenta (chkausente en lista_turnos.js)
    ATENDIDO     // Cuando pasa al consultorio
}