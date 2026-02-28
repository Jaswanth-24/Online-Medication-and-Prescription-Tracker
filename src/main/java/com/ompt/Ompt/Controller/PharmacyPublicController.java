package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.PharmacyAvailabilityDTO;
import com.ompt.Ompt.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/pharmacies")
@RequiredArgsConstructor
public class PharmacyPublicController {

    private final PharmacyService pharmacyService;

    @GetMapping("/availability")
    public List<PharmacyAvailabilityDTO> searchAvailable(
            @RequestParam String query) {
        return pharmacyService.listAvailability(query);
    }
}