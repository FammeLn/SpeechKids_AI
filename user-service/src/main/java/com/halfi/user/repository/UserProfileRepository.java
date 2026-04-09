package com.halfi.user.repository;

import com.halfi.user.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // По authUserId из JWT (sub) — основной метод для GET /users/me
    Optional<UserProfile> findByAuthUserId(Long authUserId);

    // По email — пригодится для поиска
    Optional<UserProfile> findByEmail(String email);

    // Проверка уникальности никнейма
    boolean existsByNickName(String nickName);
}
