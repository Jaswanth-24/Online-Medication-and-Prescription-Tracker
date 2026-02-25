// package com.ompt.Ompt.Controller;
//
// import com.ompt.Ompt.DTO.DoctorProfileDTO;
// import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
// import com.ompt.Ompt.DTO.DoctorRegisterRequestDTO;
// import com.ompt.Ompt.DTO.DoctorResponseDTO;
// import com.ompt.Ompt.model.User;
// import com.ompt.Ompt.repository.UserRepository;
// import com.ompt.Ompt.service.AdminDoctorService;
// import com.ompt.Ompt.service.DoctorProfileService;
// import jakarta.validation.Valid;
// import java.util.List;
// import java.util.Map;
// import lombok.AllArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException;
//
// @RestController
// @RequestMapping("/api/admin/doctors")
// @AllArgsConstructor
// public class AdminDoctorController {
//
//  private final AdminDoctorService adminDoctorService;
//  private final UserRepository userRepository;
//  private final DoctorProfileService doctorProfileService;
//
//  @PostMapping
//  public ResponseEntity<DoctorResponseDTO> registerDoctor(
//      @Valid @RequestBody DoctorRegisterRequestDTO request, Authentication authentication) {
//    User admin =
//        userRepository
//            .findByEmailIgnoreCase(authentication.getName())
//            .orElseThrow(() -> new IllegalStateException("Admin not found"));
//
//    return ResponseEntity.status(HttpStatus.CREATED)
//        .body(adminDoctorService.registerDoctor(request, admin));
//  }
//
//  @GetMapping
//  public ResponseEntity<List<DoctorProfileDTO>> listDoctors(Authentication authentication) {
//    User admin =
//        userRepository
//            .findByEmailIgnoreCase(authentication.getName())
//            .orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
//
//    return ResponseEntity.ok(adminDoctorService.listDoctors(admin));
//  }
//
//  @PutMapping("/{doctorId}/profile")
//  public ResponseEntity<Void> updateDoctorProfile(
//      @PathVariable Long doctorId,
//      @Valid @RequestBody DoctorProfileUpdateDTO dto,
//      Authentication authentication) {
//    String email = authentication.getName();
//
//    User admin =
//        userRepository
//            .findByEmailIgnoreCase(email)
//            .orElseThrow(() -> new IllegalStateException("Admin not found"));
//
//    doctorProfileService.adminUpdateDoctorProfile(admin, doctorId, dto);
//    return ResponseEntity.ok().build();
//  }
//
//  @GetMapping("/template")
//  public ResponseEntity<Map<String, Object>> getDoctorTemplate(Authentication authentication) {
//    User admin =
//        userRepository
//            .findByEmailIgnoreCase(authentication.getName())
//            .orElseThrow(() -> new IllegalStateException("Admin not found"));
//
//    return ResponseEntity.ok(adminDoctorService.getDoctorTemplateForReact());
//  }
// }
