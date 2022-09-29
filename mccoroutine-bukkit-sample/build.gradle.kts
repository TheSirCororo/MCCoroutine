import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

publishing {
    publications {
        (findByName("mavenJava") as MavenPublication).artifact(tasks.shadowJar)
    }
}

tasks.withType<ShadowJar> {
    dependsOn("jar")
    archiveClassifier.set("shadowJar")
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}.${archiveExtension.get()}")

    // Change the output folder of the plugin.
    // destinationDir = File("C:\\temp\\plugins\\")
}

repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(project(":mccoroutine-bukkit-api"))
    implementation(project(":mccoroutine-bukkit-core"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    testCompileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
}
