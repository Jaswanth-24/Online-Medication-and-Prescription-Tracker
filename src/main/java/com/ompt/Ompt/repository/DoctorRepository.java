package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Doctor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
  Optional<Doctor> findByUserId(Long userId);

  List<Doctor> findAllByUser_Hospital_Id(Long hospitalId);

  List<Doctor> findAllByProfileCompletedTrue();

  List<Doctor> findAllByHospital_IdAndProfileCompletedTrue(Long hospitalId);
}
