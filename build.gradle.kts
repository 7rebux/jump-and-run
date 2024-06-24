@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
  // compileOnly("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
  // compileOnly("com.github.azbh111:craftbukkit-1.8.8:R")
  compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
  implementation("org.slf4j:slf4j-nop:2.0.3")

  shadow("org.mariadb.jdbc:mariadb-java-client:3.4.0")
  shadow("de.tr7zw:item-nbt-api:2.13.0")
  shadow("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation(kotlin("test"))
}

tasks.named<ShadowJar>("shadowJar") {
  manifest {
    attributes(mapOf("Main-Class" to "$group/Plugin"))
  }

  archiveBaseName.set(pluginName)
  archiveClassifier.set("")
  archiveVersion.set("")
  destinationDir = File("./_server/plugins")
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
