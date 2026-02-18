package com.ompt.Ompt.service;


import com.ompt.Ompt.DTO.DoctorDegreeDTO;
import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.DTO.PublicDoctorDTO;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.DoctorDegreeRepository;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.DoctorSpecializationRepository;
import com.ompt.Ompt.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorProfileService {

    private final DoctorRepository doctorRepository;
    private final DoctorDegreeRepository degreeRepository;
    private final DoctorSpecializationRepository specializationRepository;
    private final SpecializationRepository specializationRepo;


    public DoctorProfileResponseDTO getOwnProfile(Long userId) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        return DoctorMapper.toResponseDTO(doctor);
    }

    public DoctorProfileResponseDTO getProfile(Long userId) {

        Doctor doctor = doctorRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        return DoctorMapper.toResponseDTO(doctor);
    }
    public void updateOwnProfile(Long userId, DoctorProfileUpdateDTO dto) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        updateDoctorProfile(doctor, dto);
    }

    public void adminUpdateDoctorProfile(
            User admin,
            Long doctorId,
            DoctorProfileUpdateDTO dto
    ) {
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only admin can update doctor profile");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalStateException("Doctor not found"));

        // Recommended hospital-level check
        if (!doctor.getUser().getHospital().getId()
                .equals(admin.getHospital().getId())) {
            throw new IllegalStateException("Doctor not in your hospital");
        }

        updateDoctorProfile(doctor, dto);
    }
    private void updateDoctorProfile(
            Doctor doctor,
            DoctorProfileUpdateDTO dto
    ) {
        // Core fields
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setProfileCompleted(true);

        // Degrees
        degreeRepository.deleteByDoctorId(doctor.getId());
        for (DoctorDegreeDTO d : dto.getDegrees()) {
            DoctorDegree degree = new DoctorDegree();
            degree.setDoctor(doctor);
            degree.setDegreeName(d.getDegreeName());
            degree.setInstitution(d.getInstitution());
            degree.setYearCompleted(d.getYearCompleted());
            degreeRepository.save(degree);
        }

        // Specializations
        specializationRepository.deleteByDoctorId(doctor.getId());
        for (Long specId : dto.getSpecializationIds()) {
            Specialization spec = specializationRepo.findById(specId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid specialization"));

            DoctorSpecialization ds = new DoctorSpecialization();
            ds.setDoctor(doctor);
            ds.setSpecialization(spec);
            specializationRepository.save(ds);
        }

        doctorRepository.save(doctor);
    }

    public List<PublicDoctorDTO> listPublicDoctors() {

        return doctorRepository.findAll()
                .stream()
                .filter(Doctor::isProfileCompleted)
                .map(d -> new PublicDoctorDTO(
                        d.getId(),
                        d.getUser().getName(),
                        d.getUser().getHospital().getName(),
                        d.getYearsOfExperience(),
                        specializationRepository
                                .findByDoctorId(d.getId())
                                .stream()
                                .map(ds -> ds.getSpecialization().getName())
                                .toList(),
                        new PublicDoctorDTO.Performance(
                                Double.valueOf(0.0),
                                Integer.valueOf(0)
                        )

                ))
                .toList();
    }


    public List<PublicDoctorDTO> listPublicDoctorsByHospital(Long hospitalId) {

        return doctorRepository.findByHospitalId(hospitalId)
                .stream()
                .filter(Doctor::isProfileCompleted)
                .map(d -> new PublicDoctorDTO(
                        d.getId(),
                        d.getUser().getName(),
                        d.getUser().getHospital().getName(),
                        d.getYearsOfExperience(),
                        specializationRepository
                                .findByDoctorId(d.getId())
                                .stream()
                                .map(ds -> ds.getSpecialization().getName())
                                .toList(),
                        new PublicDoctorDTO.Performance(
                                Double.valueOf(0.0),
                                Integer.valueOf(0)
                        )

                ))
                .toList();
    }
}

