package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.InformeRequestDTO;
import com.example.turnos_service.application.dto.InformeGuardadoEvent;
import com.example.turnos_service.infrastructure.persistence.InformeEstudioEntity;
import com.example.turnos_service.infrastructure.persistence.InformeEstudioRepository;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Imports para la generación del PDF
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InformeEstudioService {

    private final InformeEstudioRepository informeRepository;
    private final TurnoRepository turnoRepository;
    private final MedicoRepository medicoRepository;
    private final RabbitTemplate rabbitTemplate;

    // Motor de plantillas inyectado automáticamente por Spring Boot
    private final TemplateEngine templateEngine;

    @Transactional
    public Long guardarInforme(InformeRequestDTO request) {

        // 1. Manejo del Soft Delete si ya existía un informe
        informeRepository.findByTurnoIdAndFechaHoraBorradoSuaveIsNull(request.getIdConsulta())
                .ifPresent(informeViejo -> {
                    informeViejo.setFechaHoraBorradoSuave(LocalDateTime.now());
                    informeRepository.save(informeViejo);
                });

        // 2. Mapeo manual desde el DTO a la Entidad
        InformeEstudioEntity nuevoInforme = new InformeEstudioEntity();

        // Usamos referencias proxy para evitar SELECTs extras
        nuevoInforme.setTurno(turnoRepository.getReferenceById(request.getIdConsulta()));
        nuevoInforme.setMedico(medicoRepository.getReferenceById(request.getIdMedico()));

        nuevoInforme.setFecha(request.getFecha());
        nuevoInforme.setTitulo(request.getTitulo());
        nuevoInforme.setInformeTextual(request.getInformeTextual());
        nuevoInforme.setFechaControl(request.getFechaControl());

        // Convertimos el array de plantillas a un string separado por comas
        if (request.getPlantillasSeleccionadas() != null && !request.getPlantillasSeleccionadas().isEmpty()) {
            nuevoInforme.setPlantillasSeleccionadas(String.join(",", request.getPlantillasSeleccionadas()));
        }

        nuevoInforme.setCategoriaBIRADS(request.getCategoriaBIRADS());
        nuevoInforme.setDensidadBIRADS(request.getDensidadBIRADS());

        // 3. Guardar en Base de Datos
        InformeEstudioEntity informeGuardado = informeRepository.save(nuevoInforme);

        // 4. Publicar el evento en RabbitMQ de forma asíncrona
        InformeGuardadoEvent evento = new InformeGuardadoEvent(informeGuardado.getId(), request.getIdConsulta());
        rabbitTemplate.convertAndSend(RabbitMQConfig.INFORME_QUEUE_NAME, evento);

        return informeGuardado.getId();
    }

    public byte[] generarPdf(Long idConsulta) {
        // 1. Buscamos el informe
        InformeEstudioEntity informe = informeRepository.findByTurnoIdAndFechaHoraBorradoSuaveIsNull(idConsulta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Informe no encontrado para la consulta " + idConsulta));

        try {
            // 2. Mapeo de datos para el HTML
            Context context = new Context();
            context.setVariable("idConsulta", idConsulta);
            context.setVariable("titulo", informe.getTitulo() != null ? informe.getTitulo() : "Estudio sin título");
            context.setVariable("informeTextual", informe.getInformeTextual());
            context.setVariable("fechaEstudio", informe.getFecha().toString());

            // Datos del paciente
            String nombrePaciente = informe.getTurno().getDatosPaciente().getApellidoPaterno() + " " +
                    informe.getTurno().getDatosPaciente().getPrimerNombre();
            context.setVariable("pacienteNombre", nombrePaciente);
            context.setVariable("pacienteDni", informe.getTurno().getDatosPaciente().getNumeroDocumento());

            // Datos del médico informante
            String nombreMedico = informe.getMedico().getDatosPersonales().getApellidoPaterno() + " " +
                    informe.getMedico().getDatosPersonales().getPrimerNombre();
            context.setVariable("medicoInformante", nombreMedico);

            // 3. Procesar Thymeleaf
            String htmlProcesado = templateEngine.process("informe_medico", context);

            // 4. Renderizar a PDF
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(htmlProcesado, "/");
                builder.toStream(os);
                builder.run();
                return os.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error crítico al generar el PDF del informe", e);
        }
    }
}