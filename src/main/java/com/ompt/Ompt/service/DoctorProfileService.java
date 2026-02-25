package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.DTO.DoctorSelfProfileResponseDTO;
import com.ompt.Ompt.DTO.PublicDoctorDTO;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.DoctorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorProfileService {

  private final DoctorRepository doctorRepository;

  @Transactional
  public void updateProfile(User user, DoctorProfileUpdateDTO dto) {

    if (user.getRole() != Role.DOCTOR) {
      throw new AccessDeniedException("Only doctors can update profile");
    }

    Doctor doctor =
        doctorRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Doctor record not found"));

    // Personal
    doctor.setDob(dto.getDob());
    doctor.setGender(dto.getGender());
    doctor.setPhone(dto.getPhone());
    doctor.setAddress(dto.getAddress());
    doctor.setEmergencyContact(dto.getEmergencyContact());

    // Employment
    doctor.setDesignation(dto.getDesignation());
    doctor.setYearsOfExperience(dto.getYearsOfExperience());
    doctor.setDateOfJoining(dto.getDateOfJoining());

    // Licensing
    doctor.setLicenseNumber(dto.getLicenseNumber());

    doctor.setProfileCompleted(true);
  }

  @Transactional(readOnly = true)
  public DoctorSelfProfileResponseDTO getProfile(User user) {

    if (user.getRole() != Role.DOCTOR) {
      throw new AccessDeniedException("Only doctors can view profile");
    }

    Doctor doctor =
        doctorRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Doctor record not found"));

    DoctorSelfProfileResponseDTO dto = new DoctorSelfProfileResponseDTO();

    // 🔑 FROM USER (this was missing)
    dto.setFullName(user.getName());
    dto.setEmail(user.getEmail());

    // FROM DOCTOR
    dto.setDob(doctor.getDob());
    dto.setGender(doctor.getGender());
    dto.setPhone(doctor.getPhone());
    dto.setAddress(doctor.getAddress());
    dto.setEmergencyContact(doctor.getEmergencyContact());
    dto.setDesignation(doctor.getDesignation());
    dto.setYearsOfExperience(doctor.getYearsOfExperience());
    dto.setDateOfJoining(doctor.getDateOfJoining());
    dto.setLicenseNumber(doctor.getLicenseNumber());

    return dto;
  }

  public List<PublicDoctorDTO> listPublicDoctors() {
    return doctorRepository.findAllByProfileCompletedTrue().stream()
        .map(this::toPublicDoctorDTO)
        .toList();
  }

  public List<PublicDoctorDTO> listPublicDoctorsByHospital(Long hospitalId) {
    return doctorRepository.findAllByHospital_IdAndProfileCompletedTrue(hospitalId).stream()
        .map(this::toPublicDoctorDTO)
        .toList();
  }

  private PublicDoctorDTO toPublicDoctorDTO(Doctor doctor) {
    return new PublicDoctorDTO(
        doctor.getId(),
        doctor.getUser().getName(),
        doctor.getHospital().getName(),
        doctor.getDepartment(),
        doctor.getDesignation(),
        doctor.getYearsOfExperience(),
        new PublicDoctorDTO.Performance(doctor.getRating(), doctor.getRatingCount()));
  }
}
