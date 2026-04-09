package com.halfi.user.service;

import com.halfi.user.dto.response.UserProfileResponse;
import com.halfi.user.exception.UserException;
import com.halfi.user.model.UserProfile;
import com.halfi.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;

    // GET /users/me — ищем профиль по authUserId из JWT
    public UserProfileResponse getProfileByAuthUserId(Long authUserId) {
        UserProfile profile = profileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> UserException.notFound(authUserId));
        return UserProfileResponse.from(profile);
    }

    // Вызывается из Kafka consumer — создаём профиль при регистрации
    @Transactional
    public void createProfile(Long authUserId, String email, String nickName) {
        // Идемпотентность — Kafka может доставить повторно
        if (profileRepository.findByAuthUserId(authUserId).isPresent()) {
            log.warn("Profile already exists for authUserId={}", authUserId);
            return;
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .email(email)
                .nickName(nickName)
                .avatarUrl(null)
                .balance(0L)
                .build();

        profileRepository.save(profile);
        log.info("Created profile for authUserId={}, nick={}", authUserId, nickName);
    }
}
