package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.DTO.DoctorSelfProfileResponseDTO;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;
import com.ompt.Ompt.service.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {

  private final DoctorProfileService doctorProfileService;
  private final UserRepository userRepository;

  @PutMapping
  public ResponseEntity<Void> updateProfile(
      @RequestBody DoctorProfileUpdateDTO dto, Authentication authentication) {

    User doctor =
        userRepository
            .findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    doctorProfileService.updateProfile(doctor, dto);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<DoctorSelfProfileResponseDTO> getProfile(Authentication authentication) {

    User doctor =
        userRepository
            .findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    return ResponseEntity.ok(doctorProfileService.getProfile(doctor));
  }
}
