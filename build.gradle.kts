plugins {
    kotlin("jvm") version "2.3.0"
    id("com.github.ben-manes.versions") version "0.53.0"
}

group = "org.mongo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version = "3.1.1"

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation("org.mongodb:mongodb-driver-kotlin-sync")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.27")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}