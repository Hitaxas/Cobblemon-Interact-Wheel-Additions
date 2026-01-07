plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    enableTransitiveAccessWideners.set(true)
}

repositories {
    maven("https://maven.terraformersmc.com/releases/")
}

val shadowBundle = configurations.create("shadowBundle")

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_lang_kotl_version")}")

    implementation(project(":common", configuration = "namedElements")) { isTransitive = false }
    "developmentFabric"(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionFabric")) { isTransitive = false }

    // Fabric-specific dependencies
    modImplementation("com.cobblemon:fabric:${rootProject.property("cobblemon_version")}")
    modApi("me.shedaniel.cloth:cloth-config-fabric:${rootProject.property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    // Mods for Fabric-specific integrations
    modImplementation("com.terraformersmc:modmenu:${rootProject.property("mod_menu_version")}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks {
    processResources {
        inputs.property("mod_version", project.version)
        inputs.property("minecraft_version", rootProject.property("minecraft_version"))
        inputs.property("java_version", rootProject.property("java_version"))
        inputs.property("fabric_loader_version", rootProject.property("fabric_loader_version"))
        inputs.property("cobblemon_version", rootProject.property("cobblemon_version"))
        inputs.property("cloth_config_version", rootProject.property("cloth_config_version"))
        inputs.property("mod_menu_version", rootProject.property("mod_menu_version"))

        filesMatching("fabric.mod.json") {
            expand(project.properties)
        }
    }

    jar {
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        configurations = listOf(shadowBundle)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}
