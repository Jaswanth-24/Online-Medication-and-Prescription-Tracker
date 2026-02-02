package com.ompt.Ompt.DTO;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileResponseDTO {

    private Long doctorId;
    private String name;
    private String email;
    private Integer yearsOfExperience;
    private String licenseNumber;
    private boolean profileCompleted;

    private List<SpecializationDTO> specializations;
    private List<DoctorDegreeDTO> degrees;
}
