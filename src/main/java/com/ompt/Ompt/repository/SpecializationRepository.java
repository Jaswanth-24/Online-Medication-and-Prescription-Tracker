package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    boolean existsByNameIgnoreCase(String name);
}
