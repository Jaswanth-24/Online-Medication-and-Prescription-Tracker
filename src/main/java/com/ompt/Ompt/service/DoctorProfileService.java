package com.ompt.Ompt.service;


import com.ompt.Ompt.DTO.DoctorDegreeDTO;
import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.model.Doctor;
import com.ompt.Ompt.model.DoctorDegree;
import com.ompt.Ompt.model.DoctorSpecialization;
import com.ompt.Ompt.model.Specialization;
import com.ompt.Ompt.repository.DoctorDegreeRepository;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.DoctorSpecializationRepository;
import com.ompt.Ompt.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorProfileService {

    private final DoctorRepository doctorRepository;
    private final DoctorDegreeRepository degreeRepository;
    private final DoctorSpecializationRepository specializationRepository;
    private final SpecializationRepository specializationRepo;

    public DoctorProfileResponseDTO getProfile(Long userId) {

        Doctor doctor = doctorRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        return DoctorMapper.toResponseDTO(doctor);
    }

    public void updateProfile(Long userId, DoctorProfileUpdateDTO dto) {
        System.out.println(userId);
        Doctor doctor = doctorRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));

        // Update core fields
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setProfileCompleted(true);

        // Clear old mappings
        degreeRepository.deleteByDoctorId(doctor.getId());
        specializationRepository.deleteByDoctorId(doctor.getId());

        // Save degrees
        for (DoctorDegreeDTO d : dto.getDegrees()) {
            DoctorDegree degree = new DoctorDegree();
            degree.setDoctor(doctor);
            degree.setDegreeName(d.getDegreeName());
            degree.setInstitution(d.getInstitution());
            degree.setYearCompleted(d.getYearCompleted());
            degreeRepository.save(degree);
        }

        // Save specializations
        for (Long specId : dto.getSpecializationIds()) {
            Specialization spec = specializationRepo.findById(specId)
                    .orElseThrow(() -> new RuntimeException("Invalid specialization"));

            DoctorSpecialization ds = new DoctorSpecialization();
            ds.setDoctor(doctor);
            ds.setSpecialization(spec);
            specializationRepository.save(ds);
        }

        doctorRepository.save(doctor);
    }
}

