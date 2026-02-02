package com.ompt.Ompt.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDegreeDTO {

    @NotBlank(message = "Degree name is required")
    private String degreeName;

    @NotBlank(message = "Institution name is required")
    private String institution;

    @Min(value = 1900, message = "Year must be valid")
    @Max(value = 2100, message = "Year must be valid")
    private Integer yearCompleted;
}
