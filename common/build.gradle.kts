architectury {
    common("fabric", "neoforge")
}

dependencies {
    // Dependencies
    modImplementation("com.cobblemon:mod:${rootProject.property("cobblemon_version")}")
    modApi("me.shedaniel.cloth:cloth-config-fabric:${rootProject.property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}
