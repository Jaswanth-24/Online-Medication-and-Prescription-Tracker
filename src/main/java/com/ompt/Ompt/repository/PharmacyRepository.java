package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Pharmacy;
import com.ompt.Ompt.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
  Optional<Pharmacy> findByUserId(Long userId);

  Optional<Pharmacy> findByUser(User user);
}
