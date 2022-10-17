@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val exposedVersion: String by project

plugins {
    kotlin("jvm") version "1.7.20"
}

repositories {
    mavenCentral()

    maven {
        url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    implementation("com.github.azbh111:craftbukkit-1.8.8:R")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("org.slf4j:slf4j-nop:2.0.3")

    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
