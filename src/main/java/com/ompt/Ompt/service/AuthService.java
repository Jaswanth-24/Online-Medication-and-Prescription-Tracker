package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.Util.JwtUtil;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor

public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRecordService patientRecordService;
    private final PharmacyRepository pharmacyRepository;
    private final JwtUtil jwtUtil;
    

    public void register(RegisterRequestDTO request) {
        String email = request.getEmail().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        Hospital hospital = hospitalRepository
                .findById(request.getHospitalId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid hospital")
                );
        User doctor = userRepository
                .findById(request.getDoctorAssignedId())
                .orElseThrow(() ->new IllegalArgumentException("Invalid doctor assigned")
                );

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setHospital(hospital);

        userRepository.save(user);
        patientRecordService.createForNewPatient(user,doctor);
    }

    public User login(String email,String rawPassword){
        User user= userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(()->
                        new UsernameNotFoundException("Invalid Credentials")
                );
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new BadCredentialsException("Account not activated");
        }

        if (user.getPassword() == null) {
            throw new BadCredentialsException("Password not set");
        }

        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new BadCredentialsException("Invalid Credentials");
        }

        return user;
    }

    public void forgotPassword(String email){
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String token= UUID.randomUUID().toString();
            user.setResetTokenHash(passwordEncoder.encode(token));
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            emailService.sendResetPasswordEmail(user.getEmail(),token);
        });
    }


    public void setPassword(String token, String newPassword) {

        List<User> users = userRepository
                .findByResetTokenHashIsNotNullAndResetTokenExpiryAfter(LocalDateTime.now());

        User user = users.stream()
                .filter(u->passwordEncoder.matches(token,u.getResetTokenHash()))
                .findFirst()
                .orElseThrow(()->
                        new IllegalStateException("Invalid or Expired Token")
                );

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus(AccountStatus.ACTIVE);

        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
    public void registerDoctorSelf(
            String name,
            String email,
            String password,
            Hospital hospital
    ) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User doctorUser = new User();
        doctorUser.setName(name);
        doctorUser.setEmail(email.toLowerCase());
        doctorUser.setPassword(passwordEncoder.encode(password));
        doctorUser.setRole(Role.DOCTOR);
        doctorUser.setStatus(AccountStatus.ACTIVE);
        doctorUser.setHospital(hospital);

        userRepository.save(doctorUser);
        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        doctor.setProfileCompleted(false);
        doctorRepository.save(doctor);
    }

    public void resetPasswordWithCode(String email, String code, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Invalid or Expired Token"));

        if (user.getResetTokenHash() == null || user.getResetTokenExpiry() == null) {
            throw new IllegalStateException("Invalid or Expired Token");
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invalid or Expired Token");
        }

        if (!passwordEncoder.matches(code, user.getResetTokenHash())) {
            throw new IllegalStateException("Invalid or Expired Token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void verifyResetCode(String email, String code) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Invalid or Expired Token"));

        if (user.getResetTokenHash() == null || user.getResetTokenExpiry() == null) {
            throw new IllegalStateException("Invalid or Expired Token");
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invalid or Expired Token");
        }

        if (!passwordEncoder.matches(code, user.getResetTokenHash())) {
            throw new IllegalStateException("Invalid or Expired Token");
        }
    }


}

