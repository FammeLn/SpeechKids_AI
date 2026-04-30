rootProject.name = "Halfi_core"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "1.9.22"
        id("org.springframework.boot") version "3.4.1"
        id("io.spring.dependency-management") version "1.1.4"
    }
}

rootProject.name = "Halfi_core"


include("auth-service")
include("user-service")
include("notification-service")
include("api-gateway-service")
include("api-gateway")
include("profile-service")
include("payment-service")