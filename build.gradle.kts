plugins {
    kotlin("jvm") version "2.3.0"
    id("com.github.ben-manes.versions") version "0.53.0"
    application
}

group = "org.mongo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:3.4.0"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-html-builder-jvm")
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation("org.mongodb:mongodb-driver-kotlin-sync")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.27")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
}

application {
    mainClass.set("org.mongo.mqs.CliKt")
}

distributions {
    main {
        distributionBaseName.set("cli")
    }
}

tasks.test {
    useJUnitPlatform()
}