package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientProfile extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUser(User user);
}