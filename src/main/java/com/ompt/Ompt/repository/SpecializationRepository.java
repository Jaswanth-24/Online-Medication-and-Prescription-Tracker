package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Specialization> findByNameIgnoreCase(String name);
}
