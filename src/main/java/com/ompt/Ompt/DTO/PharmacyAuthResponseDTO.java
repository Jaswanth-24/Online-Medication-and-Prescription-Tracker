package com.ompt.Ompt.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PharmacyAuthResponseDTO {
    private String token;
    private PharmacyProfileDTO pharmacy;
}
