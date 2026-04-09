package com.halfi.notification.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.halfi.notification.dto.VerificationMessage
import com.halfi.notification.service.EmailService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumer(private val emailService: EmailService) {

    private val mapper = jacksonObjectMapper()

    @KafkaListener(topics = ["email.send.verification"], groupId = "notification-group")
    fun listen(message: String) {
        println("KafkaConsumer: Received message: $message")
        try {
            val dto = mapper.readValue<VerificationMessage>(message)
            emailService.sendVerificationEmail(dto.email, dto.code, dto.type)
        } catch (e: Exception) {
            System.err.println("Error processing Kafka message: ${e.message}")
        }
    }
}
