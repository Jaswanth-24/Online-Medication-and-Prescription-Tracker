package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.DoctorDegree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorDegreeRepository extends JpaRepository<DoctorDegree, Long> {

    List<DoctorDegree> findByDoctorId(Long doctorId);

    void deleteByDoctorId(Long doctorId);
}
