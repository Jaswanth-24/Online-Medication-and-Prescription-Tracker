package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.service.PharmacyAuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/pharmacy")
@AllArgsConstructor
public class PharmacyAuthController {

  private final PharmacyAuthService pharmacyAuthService;

  @PostMapping("/register")
  public ResponseEntity<PharmacyProfileDTO> register(
      @Valid @RequestBody PharmacyRegisterRequestDTO request) {
    PharmacyProfileDTO response = pharmacyAuthService.pharmacyRegister(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<PharmacyAuthResponseDTO> login(
      @Valid @RequestBody PharmacyLoginRequestDTO request) {
    PharmacyAuthResponseDTO response = pharmacyAuthService.pharmacyLogin(request);
    return ResponseEntity.ok(response);
  }
}
