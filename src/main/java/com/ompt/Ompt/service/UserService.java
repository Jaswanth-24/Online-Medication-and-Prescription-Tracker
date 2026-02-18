package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.MeResponseDTO;
import com.ompt.Ompt.model.Pharmacy;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.PharmacyRepository;
import com.ompt.Ompt.repository.UserProfileRepository;
import com.ompt.Ompt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class UserService {
    private final DoctorRepository doctorRepository;
    private final PharmacyRepository pharmacyRepository;
    private final UserProfileRepository userProfileRepository;

    public MeResponseDTO buildMeResponse(User user) {

        MeResponseDTO dto = new MeResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setHospitalName(
                user.getHospital() != null ? user.getHospital().getName() : null
        );

        switch (user.getRole()) {

            case DOCTOR -> {
                doctorRepository.findByUserId(user.getId())
                        .ifPresent(d -> {
                            dto.setProfileCompleted(d.isProfileCompleted());
                            dto.setDoctorId(d.getId());
                        });
            }

            case PATIENT -> {
                userProfileRepository.findByUser(user)
                        .ifPresent(p -> {
                            dto.setAge(p.getAge());
                            dto.setGender(p.getGender());
                        });
            }

            case PHARMACY -> {
                pharmacyRepository.findByUserId(user.getId())
                        .ifPresent(p -> dto.setPharmacyName(p.getPharmacyName()));
            }

            case ADMIN -> {
                // nothing extra
            }
        }

        return dto;
    }
}
