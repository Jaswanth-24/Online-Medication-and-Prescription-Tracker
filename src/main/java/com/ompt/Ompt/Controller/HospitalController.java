package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.OrganizationRegisterRequest;
import com.ompt.Ompt.DTO.OrganizationResponse;
import com.ompt.Ompt.service.HospitalService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Getter
@AllArgsConstructor
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final HospitalService hospitalService;

    @PostMapping("/register")
    public ResponseEntity<OrganizationResponse> register(
            @Valid @RequestBody OrganizationRegisterRequest request
    ) {
        OrganizationResponse response =
                hospitalService.registerHospital(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
