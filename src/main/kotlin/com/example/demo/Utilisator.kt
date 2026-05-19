package com.example.demo

import com.fasterxml.jackson.annotation.JsonCreator

enum class Utilisator {
    PARENT,
    CHILD,
    THERAPIST,
    ADMIN;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): Utilisator {
            return values().firstOrNull {
                it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Неверная роль: '$value'. Доступные роли: ${values().map { it.name }}"
            )
        }
    }
}