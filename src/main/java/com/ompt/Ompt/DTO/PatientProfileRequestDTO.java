package com.ompt.Ompt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientProfileRequest {

    @NotBlank
    private String patientName;

    @NotBlank
    private String gender;

    @NotNull(message = "Age is required")
    private Integer age;

    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;

    @NotBlank
    private String bloodGroup;

    @NotBlank
    private String address;
}
