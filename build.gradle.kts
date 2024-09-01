@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginName = "JumpAndRun"
val exposedVersion: String by project

plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
  kotlin("jvm") version "1.7.20"
}

group = "net.rebux.jumpandrun"
version = "2.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
  compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
  compileOnly("com.github.azbh111:craftbukkit-1.8.8:R")
  compileOnly("de.tr7zw:item-nbt-api-plugin:2.13.2")

  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

  implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")

  testImplementation(kotlin("test"))
}

tasks.named<ShadowJar>("shadowJar") {
  manifest {
    attributes(mapOf("Main-Class" to "$group/Plugin"))
  }

  archiveClassifier.set("")
}

tasks.build {
  dependsOn("shadowJar")
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
