package com.halfi.user.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Должен совпадать с com.halfi.auth.messaging.dto.UserRegisteredEvent
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private Long   userId;
    private String email;
    private String nickName;
    private String verificationCode;
}
