package com.ompt.Ompt.DTO;

import lombok.*;

import java.util.List;
@Data
public class PublicDoctorDTO {

    private Long doctorId;
    private String name;
    private String hospitalName;
    private Long hospitalId;
    private Integer yearsOfExperience;
    private List<String> specializations;
    private Performance performance;
    public PublicDoctorDTO(
            Long doctorId,
            String name,
            String hospitalName,
            Integer yearsOfExperience,
            List<String> specializations,
            Performance performance
    ) {
        this.doctorId = doctorId;
        this.name = name;
        this.hospitalName = hospitalName;
        this.yearsOfExperience = yearsOfExperience;
        this.specializations = specializations;
        this.performance = performance;
    }
    @Data
    @AllArgsConstructor
    public static class Performance {
        private Double rating;
        private Integer ratingCount;
    }
}
