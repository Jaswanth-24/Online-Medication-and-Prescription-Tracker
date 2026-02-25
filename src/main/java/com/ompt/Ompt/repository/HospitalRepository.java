package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Hospital;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

  Optional<Hospital> findByNameIgnoreCase(String name);
}
