package com.example.demo.entity
import com.example.demo.Utilisator
import jakarta.persistence.*

@Entity
@Table(name = "Profile")
class Profile (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id: Long = 0,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var surname: String = "",
    @Column(nullable = true)
    var fatherName: String = "",
    @Column(nullable = false)
    var number: String = "",
    @Column(nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var password: String = "",
    @Column(nullable = false)
    var role: Utilisator = Utilisator.PARENT
)