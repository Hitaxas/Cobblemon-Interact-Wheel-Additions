import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    kotlin("jvm") version ("2.2.20")

    id("dev.architectury.loom") version ("1.11-SNAPSHOT") apply false
    id("architectury-plugin") version ("3.4-SNAPSHOT")
    id("com.github.johnrengelman.shadow") version ("8.1.1") apply false
}

architectury {
    minecraft = "${rootProject.property("minecraft_version")}"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "architectury-plugin")

    group = "${rootProject.property("maven_group")}"
    version = "${rootProject.property("mod_version")}+${rootProject.property("minecraft_version")}${
        if (rootProject.property("is_snapshot") == "true") {
            "-SNAPSHOT"
        } else {
            ""
        }
    }"
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    base {
        // Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
        archivesName = "${rootProject.property("archives_name")}-${project.name}"
    }

    repositories {
        mavenCentral()
        maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://maven.shedaniel.me/")
    }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
        "mappings"(loom.officialMojangMappings())
    }

    java {
        withSourcesJar()

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
