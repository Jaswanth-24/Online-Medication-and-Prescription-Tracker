package com.ompt.Ompt.repository;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.model.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
  Optional<UserProfile> findByUser(User user);
}
