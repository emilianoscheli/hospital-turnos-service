package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // <--- FUNDAMENTAL PARA JACKSON
@AllArgsConstructor // <--- EXIGIDO AL USAR NOARGS JUNTO A BUILDER
public class CrypticPayloadDTO {
    private String id;
    private String descr;
    private String date;
    private String starttime;
    private String patid;
    private String patname;
    private String patsex;
    private String patbd;
    private String origin;
    private String modality;
    private String aet;
    private String referring;
    private String clinicaldata;
    private String studyinstanceuid;
    private String status;
}