package com.ompt.Ompt.DTO;

import com.ompt.Ompt.model.Role;
import lombok.Data;

@Data
public class MeResponseDTO {

    // ─────────────────────────────
    // Common (ALL roles)
    // ─────────────────────────────
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String hospitalName;

    // ─────────────────────────────
    // Doctor-specific
    // ─────────────────────────────
    private Long doctorId;
    private Boolean profileCompleted;
    private Integer yearsOfExperience;
    private Double rating;
    private Integer ratingCount;

    // ─────────────────────────────
    // Patient-specific
    // ─────────────────────────────
    private Integer age;
    private String gender;
    private String bloodGroup;

    // ─────────────────────────────
    // Pharmacy-specific
    // ─────────────────────────────
    private Long pharmacyId;
    private String pharmacyName;
    private String pharmacyLocation;

    // ─────────────────────────────
    // Admin-specific
    // ─────────────────────────────
    private Boolean isAdmin;
}
