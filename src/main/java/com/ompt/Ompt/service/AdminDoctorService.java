package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.DoctorRegisterRequestDTO;
import com.ompt.Ompt.DTO.DoctorResponseDTO;
import com.ompt.Ompt.model.AccountStatus;
import com.ompt.Ompt.model.Doctor;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.UserRepository;
import com.ompt.Ompt.security.JwtUserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminDoctorService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;

    public DoctorResponseDTO registerDoctor(
            DoctorRegisterRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {


        User admin = userRepository
                .findById(principal.getUserId())
                .orElseThrow(() -> new IllegalStateException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only admin can register doctors");
        }

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Doctor email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setStatus(AccountStatus.PENDING);
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(null);
        user.setRole(Role.DOCTOR);
        user.setHospital(admin.getHospital());

        String token = UUID.randomUUID().toString();
        user.setResetTokenHash(passwordEncoder.encode(token));
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setHospital(admin.getHospital());
        doctor.setProfileCompleted(false);

        doctorRepository.save(doctor);

        emailService.sendDoctorWelcomeMail(
                user.getEmail(),
                admin.getHospital().getName(),
                token
        );

        return new DoctorResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                admin.getHospital().getName(),
                true
        );
    }

}
