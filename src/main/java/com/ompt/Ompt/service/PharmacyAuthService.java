package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.PharmacyAuthResponseDTO;
import com.ompt.Ompt.DTO.PharmacyLoginRequestDTO;
import com.ompt.Ompt.DTO.PharmacyProfileDTO;
import com.ompt.Ompt.DTO.PharmacyRegisterRequestDTO;
import com.ompt.Ompt.Util.JwtUtil;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.HospitalRepository;
import com.ompt.Ompt.repository.PharmacyRepository;
import com.ompt.Ompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PharmacyAuthService {

  private final UserRepository userRepository;
  private final PharmacyRepository pharmacyRepository;
  private final HospitalRepository hospitalRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public PharmacyProfileDTO pharmacyRegister(PharmacyRegisterRequestDTO request) {

    String email = request.getEmail().toLowerCase();

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("Email already registered");
    }
    Hospital hospital =
        hospitalRepository
            .findById(request.getHospitalId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid hospital"));

    User user = new User();
    user.setName(request.getPharmacyName());
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.PHARMACY);
    user.setStatus(AccountStatus.ACTIVE);
    user.setHospital(hospital);
    userRepository.save(user);

    Pharmacy pharmacy = new Pharmacy();
    pharmacy.setUser(user);
    pharmacy.setPharmacyName(request.getPharmacyName());
    pharmacy.setLocation(request.getLocation());

    pharmacyRepository.save(pharmacy);

    return new PharmacyProfileDTO(
        pharmacy.getId(),
        pharmacy.getPharmacyName(),
        pharmacy.getLocation(),
        pharmacy.getUser().getEmail());
  }

  public PharmacyAuthResponseDTO pharmacyLogin(PharmacyLoginRequestDTO request) {

    User user =
        userRepository
            .findByEmailIgnoreCase(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (user.getRole() != Role.PHARMACY) {
      throw new BadCredentialsException("Invalid pharmacy credentials");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }
    Pharmacy pharmacy =
        pharmacyRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Pharmacy profile not found"));
    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

    return new PharmacyAuthResponseDTO(
        token,
        new PharmacyProfileDTO(
            pharmacy.getId(), pharmacy.getPharmacyName(), pharmacy.getLocation(), user.getEmail()));
  }
}
