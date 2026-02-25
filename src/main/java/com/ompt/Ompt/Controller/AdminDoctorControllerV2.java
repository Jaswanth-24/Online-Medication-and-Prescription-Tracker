package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.DoctorRegisterRequestDTO;
import com.ompt.Ompt.DTO.DoctorResponseDTO;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;
import com.ompt.Ompt.service.AdminDoctorService;
import com.ompt.Ompt.service.DoctorProfileService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorControllerV2 {

  private final AdminDoctorService adminDoctorService;
  private final UserRepository userRepository;
  private final DoctorProfileService doctorProfileService;

  @PostMapping
  public ResponseEntity<DoctorResponseDTO> createDoctor(
      @Valid @RequestBody DoctorRegisterRequestDTO dto, Authentication auth) {

    User admin =
        userRepository
            .findByEmailIgnoreCase(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(adminDoctorService.registerDoctor(dto, admin));
  }

  @GetMapping
  public ResponseEntity<List<DoctorResponseDTO>> list(Authentication auth) {

    User admin =
        userRepository
            .findByEmailIgnoreCase(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    return ResponseEntity.ok(adminDoctorService.listDoctors(admin));
  }

  @GetMapping("/{doctorId}")
  public DoctorProfileResponseDTO getDoctorProfileForAdmin(
      @PathVariable Long doctorId, Authentication authentication) {

    User admin =
        userRepository
            .findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new BadCredentialsException("Doctor Not Found"));

    return adminDoctorService.getDoctorProfileForAdmin(admin, doctorId);
  }
}
