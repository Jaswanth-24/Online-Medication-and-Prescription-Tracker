package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.RegisterRequestDTO;
import com.ompt.Ompt.model.AccountStatus;
import com.ompt.Ompt.model.Hospital;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.repository.HospitalRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor

public class AuthService {
    
    private final UserRepository userrepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final HospitalRepository hospitalRepository;

    

    public void register(RegisterRequestDTO request) {
        String email = request.getEmail().toLowerCase();

        if (userrepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        Hospital hospital = hospitalRepository
                .findByNameIgnoreCase(request.getHospitalName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid hospital")
                );

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setHospital(hospital);
        userrepo.save(user);
    }

    public User login(String email,String rawPassword){
        User user=userrepo
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
        userrepo.findByEmailIgnoreCase(email).ifPresent(user -> {
            String token= UUID.randomUUID().toString();
            user.setResetTokenHash(passwordEncoder.encode(token));
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userrepo.save(user);
            emailService.sendResetPasswordEmail(user.getEmail(),token);
        });
    }

    public void resetPassword(String token, String newPassword) {
        List<User> users=userrepo.findByResetTokenHashIsNotNullAndResetTokenExpiryAfter(LocalDateTime.now());

        User user=users
                .stream()
                .filter(u->passwordEncoder.matches(token,u.getResetTokenHash()))
                .findFirst()
                .orElseThrow(()->
                        new IllegalStateException("Invalid or Expired Token")
                );

        if(!passwordEncoder.matches(token,user.getResetTokenHash())){
            throw new IllegalStateException("Invalid or expired token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);
        userrepo.save(user);
    }
    public void setPassword(String token, String newPassword) {

        List<User> users = userrepo
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

        userrepo.save(user);
    }

}

