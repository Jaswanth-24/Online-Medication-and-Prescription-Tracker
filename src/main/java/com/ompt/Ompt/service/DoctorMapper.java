package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.model.Doctor;


public class DoctorMapper {

    public static DoctorProfileResponseDTO toResponseDTO(Doctor doctor) {
        DoctorProfileResponseDTO dto = new DoctorProfileResponseDTO();

        dto.setDoctorId(doctor.getId());
        dto.setFullName(doctor.getUser().getName());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setPhone(null);

        dto.setDepartment(doctor.getDepartment());
        dto.setDesignation(doctor.getDesignation());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        dto.setLicenseNumber(doctor.getLicenseNumber());

        dto.setHospitalId(doctor.getUser().getHospital().getId());
        dto.setHospitalName(doctor.getUser().getHospital().getName());

        return dto;
    }
}
