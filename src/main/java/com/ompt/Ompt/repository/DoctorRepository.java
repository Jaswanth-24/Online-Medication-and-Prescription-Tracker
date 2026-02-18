package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findAllByUser_Hospital_Id(Long hospitalId);
    boolean existsByUserId(Long userId);
    List<Doctor> findByHospitalId(Long hospitalId);

}


