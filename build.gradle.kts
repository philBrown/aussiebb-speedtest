plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "au.id.philipbrown"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.11.0")
    implementation("com.influxdb:influxdb-client-kotlin:6.10.0")
}


kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}