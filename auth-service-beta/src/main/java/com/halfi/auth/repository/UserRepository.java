package com.halfi.auth.repository;

import com.halfi.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по email — используется в login и при регистрации (проверка дубля)
    Optional<User> findByEmail(String email);

    // Проверка существования email — для forgot-password (не раскрывает есть ли юзер)
    boolean existsByEmail(String email);
}