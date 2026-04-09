plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "com.halfi"
version = "1.0.0"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories { mavenCentral() }

dependencies {
    // ── Web — нужен только для health endpoint ──────────────────────
    implementation("org.springframework.boot:spring-boot-starter-web")

    // ── Email — главная зависимость этого сервиса ───────────────────
    // JavaMailSender, SimpleMailMessage, MimeMessage
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // ── Kafka Consumer — слушаем user.registered ────────────────────
    implementation("org.springframework.kafka:spring-kafka")

    // ── Kotlin + JSON ────────────────────────────────────────────────
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // ── Lombok ───────────────────────────────────────────────────────
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ── Tests ────────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
