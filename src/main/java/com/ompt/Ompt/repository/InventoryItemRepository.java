package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.InventoryItem;
import com.ompt.Ompt.model.Pharmacy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
  List<InventoryItem> findByPharmacyOrderByIdDesc(Pharmacy pharmacy);

  Optional<InventoryItem> findByIdAndPharmacy(Long id, Pharmacy pharmacy);

  List<InventoryItem> findByNameIgnoreCaseAndQuantityGreaterThan(String name, int quantity);

  Optional<InventoryItem> findByPharmacy(Pharmacy pharmacy);
}
