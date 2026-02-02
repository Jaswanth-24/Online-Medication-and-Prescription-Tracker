package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.DoctorProfileResponseDTO;
import com.ompt.Ompt.DTO.DoctorProfileUpdateDTO;
import com.ompt.Ompt.security.JwtUserPrincipal;
import com.ompt.Ompt.service.DoctorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/doctor/profile")
@RequiredArgsConstructor
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    @GetMapping
    public ResponseEntity<DoctorProfileResponseDTO> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                doctorProfileService.getProfile(principal.getUserId())
        );
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody DoctorProfileUpdateDTO dto
    ) {
        doctorProfileService.updateProfile(principal.getUserId(), dto);
        return ResponseEntity.ok().build();
    }
}
