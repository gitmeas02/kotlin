plugins {
    // Apply Kotlin JVM plugin
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.4"
    application
}
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

// Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

// Security
    implementation("org.springframework.boot:spring-boot-starter-security")

// JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

// Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

// Database
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")

// Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
// JWT Library (jjwt is a popular choice)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
// Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

// Set Java toolchain to 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Application main class
application {
    mainClass.set("org.example.AppKt")
}

// Use JUnit Platform for tests
tasks.test {
    useJUnitPlatform()
}
