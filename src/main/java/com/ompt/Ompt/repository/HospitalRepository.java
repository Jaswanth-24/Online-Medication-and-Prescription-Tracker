package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Hospitals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Hospitals, Long> {

    Optional<Hospitals> findByName(String name);
}
