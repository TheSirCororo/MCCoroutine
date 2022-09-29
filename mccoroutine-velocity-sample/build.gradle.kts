plugins {
    kotlin("kapt") version "1.7.10"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    maven("https://nexus.velocitypowered.com/repository/maven-public")
}

tasks {
    shadowJar {
        dependsOn("jar")
        archiveClassifier.set("shadowJar")
        archiveFileName.set("$baseName-$version.$extension")

        // Change the output folder of the plugin.
        // destinationDirectory = file("C:\\temp\\Velocity\\plugins")
    }
}

dependencies {
    implementation(project(":mccoroutine-velocity-api"))
    implementation(project(":mccoroutine-velocity-core"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.9")

    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")
    testCompileOnly("com.velocitypowered:velocity-api:3.0.1")
}
