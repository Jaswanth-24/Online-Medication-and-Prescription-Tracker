package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.DoctorRegisterRequestDTO;
import com.ompt.Ompt.DTO.DoctorResponseDTO;
import com.ompt.Ompt.security.JwtUserPrincipal;
import com.ompt.Ompt.service.AdminDoctorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/doctors")
@AllArgsConstructor
public class AdminDoctorController {

    private final AdminDoctorService adminDoctorService;

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> registerDoctor(
            @Valid @RequestBody DoctorRegisterRequestDTO request,
           @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        DoctorResponseDTO response = adminDoctorService.registerDoctor(request, principal);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
