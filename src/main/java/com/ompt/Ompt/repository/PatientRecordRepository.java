package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.PatientRecord;
import com.ompt.Ompt.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
  Optional<PatientRecord> findByUser(User user);

  List<PatientRecord> findByUser_Hospital_Id(Long hospitalId);

  @Query("SELECT p FROM PatientRecord p WHERE p.assignedDoctor.id = :doctorId")
  List<PatientRecord> findByAssignedDoctorId(@Param("doctorId") Long doctorId);
}
