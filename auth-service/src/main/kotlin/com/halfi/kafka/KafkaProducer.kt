package com.halfi.auth.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    
    fun sendVerificationEmail(email: String, code: String, type: String = "REGISTRATION") {
        // Простой JSON строка для отправки, включая тип
        val payload = """{"email":"$email","code":"$code","type":"$type"}"""
        kafkaTemplate.send("email.send.verification", payload)
        println("Kafka: Sent $type code to $email -> $code")
    }
}
