package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PHARMACY')")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    /* =========================
       PROFILE
       ========================= */

    @GetMapping("/profile")
    public ResponseEntity<PharmacyProfileDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                pharmacyService.getProfile(authentication.getName())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<PharmacyProfileDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody PharmacyProfileUpdateDTO request
    ) {
        return ResponseEntity.ok(
                pharmacyService.updateProfile(authentication.getName(), request)
        );
    }

    /* =========================
       INVENTORY
       ========================= */

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryItemResponseDTO>> listInventory(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                pharmacyService.listInventory(authentication.getName())
        );
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryItemResponseDTO> createInventoryItem(
            Authentication authentication,
            @Valid @RequestBody InventoryItemRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                pharmacyService.createInventoryItem(authentication.getName(), request)
        );
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<InventoryItemResponseDTO> updateInventoryItem(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequestDTO request
    ) {
        return ResponseEntity.ok(
                pharmacyService.updateInventoryItem(authentication.getName(), id, request)
        );
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventoryItem(
            Authentication authentication,
            @PathVariable Long id
    ) {
        pharmacyService.deleteInventoryItem(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       DELIVERIES
       ========================= */

    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryResponseDTO>> listDeliveries(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                pharmacyService.listDeliveries(authentication.getName())
        );
    }

    @PostMapping("/deliveries")
    public ResponseEntity<DeliveryResponseDTO> createDelivery(
            Authentication authentication,
            @Valid @RequestBody DeliveryRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                pharmacyService.createDelivery(authentication.getName(), request)
        );
    }

    @PatchMapping("/deliveries/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody DeliveryStatusUpdateDTO request
    ) {
        return ResponseEntity.ok(
                pharmacyService.updateDeliveryStatus(authentication.getName(), id, request)
        );
    }
}
