package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;
import com.ompt.Ompt.service.DoctorProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<DoctorProfileResponseDTO> getProfile(
            Authentication authentication
    ) {

        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(
                doctorProfileService.getOwnProfile(user.getId())
        );
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            Authentication authentication,
            @Valid @RequestBody DoctorProfileUpdateDTO dto
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        doctorProfileService.updateOwnProfile(user.getId(), dto);
        return ResponseEntity.ok().build();
    }
}
