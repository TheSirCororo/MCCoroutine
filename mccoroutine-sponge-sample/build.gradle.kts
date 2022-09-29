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
    //  destinationDir = File("C:\\temp\\Sponge\\Sponge-2825-7.1.6\\mods")
}

repositories {
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    implementation(project(":mccoroutine-sponge-api"))
    implementation(project(":mccoroutine-sponge-core"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    compileOnly("com.google.guava:guava:31.1-jre")
    compileOnly("org.spongepowered:spongeapi:9.0.0")
    testCompileOnly("org.spongepowered:spongeapi:9.0.0")
}
