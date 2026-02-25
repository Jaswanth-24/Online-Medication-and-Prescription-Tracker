package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Delivery;
import com.ompt.Ompt.model.Pharmacy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  List<Delivery> findByPharmacyOrderByPrescribedAtDesc(Pharmacy pharmacy);

  Optional<Delivery> findByIdAndPharmacy(Long id, Pharmacy pharmacy);

  Optional<Delivery> findByPharmacy(Pharmacy pharmacy);
}
