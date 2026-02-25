package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.Util.JwtUtil;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.HospitalRepository;
import com.ompt.Ompt.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtUtil jwtUtil;
  private final HospitalRepository hospitalRepository;

  // user register
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // Doctor Self Register
  @PostMapping("/register-doctor")
  public ResponseEntity<Void> registerDoctor(@Valid @RequestBody DoctorSelfRegisterDTO request) {
    var hospital =
        hospitalRepository
            .findById(request.getHospitalId())
            .orElseThrow(() -> new IllegalStateException("Invalid hospital"));
    authService.registerDoctorSelf(
        request.getName(), request.getEmail(), request.getPassword(), hospital);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // user login
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
    User user = authService.login(request.getEmail(), request.getPassword());
    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    return ResponseEntity.ok(new LoginResponseDTO(token));
  }

  // forgot password
  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO request) {
    authService.forgotPassword(request.getEmail());
    return ResponseEntity.ok().build();
  }

  // Reset Password using Code
  @PostMapping("/verify-reset-code")
  public ResponseEntity<Void> verifyResetCode(@Valid @RequestBody VerifyResetCodeDTO request) {
    authService.verifyResetCode(request.getEmail(), request.getCode());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password-code")
  public ResponseEntity<Void> resetPasswordWithCode(
      @Valid @RequestBody ResetPasswordWithCodeDTO request) {
    authService.resetPasswordWithCode(
        request.getEmail(), request.getCode(), request.getNewPassword());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/set-password")
  public ResponseEntity<Void> setPassword(@Valid @RequestBody SetPasswordRequestDTO request) {
    authService.setPassword(request.getToken(), request.getNewPassword());
    return ResponseEntity.ok().build();
  }
}
