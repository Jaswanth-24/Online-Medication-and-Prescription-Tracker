package com.ompt.Ompt.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DoctorProfileUpdateDTO {

    @Min(0)
    private Integer yearsOfExperience;

    @NotBlank
    private String licenseNumber;

    @NotEmpty
    private List<Long> specializationIds;

    @NotEmpty
    private List<DoctorDegreeDTO> degrees;
}
