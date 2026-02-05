plugins {
    // Apply Kotlin JVM plugin
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.3"
    // Application plugin
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.3") // <-- version required
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    // dev tools 
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre11")

    // Utilities
    implementation("com.google.guava:guava:32.1.2-jre")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
