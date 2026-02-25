package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Specialization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
  boolean existsByNameIgnoreCase(String name);

  Optional<Specialization> findByNameIgnoreCase(String name);
}
