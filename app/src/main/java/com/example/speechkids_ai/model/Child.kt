package com.example.speechkids_ai.model

data class Child(
    val id: String,
    val name: String,
    val age: Int,
    val avatar: String? = null,
    val progress: Int = 0,  // 0-100
    val streak: Int = 0,
    val lastActivityDate: Long? = null
)

data class Activity(
    val id: String,
    val name: String,
    val type: String,  // exercise, game, etc.
    val duration: Int,  // in minutes
    val difficulty: String,  // easy, medium, hard
    val description: String = "",
    val iconUrl: String? = null
)

data class Session(
    val id: String,
    val childId: String,
    val activityId: String,
    val date: Long,
    val duration: Int,
    val completed: Boolean = false,
    val notes: String = ""
)

