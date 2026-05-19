package com.example.demo.entity

import com.example.demo.Language
import com.example.demo.Utilisator
import jakarta.persistence.*

@Entity
@Table(name = "ChildProfile")
class ChildProfile (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var surname: String = "",
    @Column(nullable = false)
    var diagnose: String = "",
    @Column(nullable = false)
    var language: Language = Language.RUSSIAN,
    @Column(nullable = false)
    val relo: Utilisator = Utilisator.CHILD,
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    var parent: Profile
)