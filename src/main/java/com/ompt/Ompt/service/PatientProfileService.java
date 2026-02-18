package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.PatientProfileRequestDTO;
import com.ompt.Ompt.DTO.PatientProfileResponseDTO;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.model.PatientProfile;
import com.ompt.Ompt.repository.PatientProfileRepository;
import com.ompt.Ompt.repository.UserRepository;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class PatientProfileService {
    private UserRepository userRepository;
    private final PatientProfileRepository profileRepository;

    @Transactional
    public void saveProfile(String email, PatientProfileRequestDTO request) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PatientProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElse(new PatientProfile());

        profile.setUser(user);
        profile.setPatientName(request.getPatientName());
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setAddress(request.getAddress());
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        patientProfile.setPatientName(request.getPatientName());

        profileRepository.save(profile);
    }


    public PatientProfileResponseDTO getProfile(Long userId) {

        PatientProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Profile not found")
                );

        return PatientProfileResponseDTO.from(profile);
    }


}
