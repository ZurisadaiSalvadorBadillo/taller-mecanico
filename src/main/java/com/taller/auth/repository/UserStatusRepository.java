package com.taller.auth.repository;

import com.taller.auth.model.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByValue(String value);
    List<UserStatus> findByValueNotOrderByValueAsc(String value);
}
