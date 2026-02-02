package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.DoctorDegreeDTO;
import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.SpecializationDTO;
import com.ompt.Ompt.model.Doctor;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorMapper {

    public static DoctorProfileResponseDTO toResponseDTO(Doctor doctor) {

        return new DoctorProfileResponseDTO(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getYearsOfExperience(),
                doctor.getLicenseNumber(),
                doctor.isProfileCompleted(),
                mapSpecializations(doctor),
                mapDegrees(doctor)
        );
    }
    private static List<SpecializationDTO> mapSpecializations(Doctor doctor) {

        if (doctor.getSpecializations() == null) {
            return List.of();
        }

        return doctor.getSpecializations()
                .stream()
                .map(ds -> new SpecializationDTO(
                        ds.getSpecialization().getId(),
                        ds.getSpecialization().getName()
                ))
                .collect(Collectors.toList());
    }

    private static List<DoctorDegreeDTO> mapDegrees(Doctor doctor) {

        if (doctor.getDegrees() == null) {
            return List.of();
        }

        return doctor.getDegrees()
                .stream()
                .map(d -> new DoctorDegreeDTO(
                        d.getDegreeName(),
                        d.getInstitution(),
                        d.getYearCompleted()
                ))
                .collect(Collectors.toList());
    }
}
