package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorSpecializationRepository
        extends JpaRepository<DoctorSpecialization, Long> {

    List<DoctorSpecialization> findByDoctorId(Long doctorId);

    void deleteByDoctorId(Long doctorId);
}
