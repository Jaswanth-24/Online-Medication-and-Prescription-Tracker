package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.DoctorSpecialization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Long> {

  List<DoctorSpecialization> findByDoctorId(Long doctorId);

  void deleteByDoctorId(Long doctorId);
}
