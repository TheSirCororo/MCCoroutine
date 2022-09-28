repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(project(":mccoroutine-bukkit-api"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")

    testCompileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testCompileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
}
