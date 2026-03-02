plugins {
    java
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// Группа и версия проекта
group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}