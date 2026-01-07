pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "cobblemon_iwa"

listOf(
    "common",
    "fabric",
    "neoforge"
).forEach { include(it) }