package com.halfi.user.dto.response;

import com.halfi.user.model.UserProfile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private String id;          // authUserId как строка
    private String email;
    private String avatarUrl;   // null если нет
    private Long   balance;
    private String nickName;

    // Статический маппер UserProfile -> DTO
    public static UserProfileResponse from(UserProfile p) {
        return UserProfileResponse.builder()
                .id(p.getAuthUserId().toString())
                .email(p.getEmail())
                .avatarUrl(p.getAvatarUrl())
                .balance(p.getBalance())
                .nickName(p.getNickName())
                .build();
    }
}
