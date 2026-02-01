package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.DoctorRegisterRequestDTO;
import com.ompt.Ompt.DTO.DoctorResponseDTO;
import com.ompt.Ompt.model.Hospitals;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminDoctorService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public DoctorResponseDTO registerDoctor(
            DoctorRegisterRequestDTO request,
            Authentication authentication
    ) {

        User admin = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only admin can register doctors");
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Doctor email already exists");
        }

        User doctor = new User();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail().toLowerCase());
        doctor.setRole(Role.DOCTOR);
        doctor.setHospitals(admin.getHospitals());

        String token = UUID.randomUUID().toString();
        doctor.setResetTokenHash(token); // hash if needed
        doctor.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(doctor);

        emailService.sendDoctorWelcomeMail(
                doctor.getEmail(),
                admin.getHospitals().getName(),
                token
        );

        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getEmail(),
                admin.getHospitals().getName(),
                true
        );
    }
}
