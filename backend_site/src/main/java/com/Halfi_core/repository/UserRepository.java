package com.Halfi_core.repository;

import com.Halfi_core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring сам поймет, что нужно искать по полю email
    Optional<User> findByEmail(String email);
}