package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Pharmacy;
import com.ompt.Ompt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    Optional<Pharmacy> findByUserId(Long userId);
    Optional<Pharmacy> findByUser(User user);

}

