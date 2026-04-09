package com.halfi.user.model;

import jakarta.persistence.*;   // @Entity, @Table, @Column, @Id, @GeneratedValue
import lombok.*;                // @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID пользователя из auth-service (другая БД — не FK!)
    @Column(name = "auth_user_id", unique = true, nullable = false)
    private Long authUserId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "nick_name", unique = true, nullable = false)
    private String nickName;

    @Column(name = "avatar_url")
    private String avatarUrl;   // null если нет

    @Column(nullable = false)
    @Builder.Default
    private Long balance = 0L;  // 0 при создании
}
