repositories {
    maven {
        url = uri("https://repo.spongepowered.org/maven")
    }
}

dependencies {
    implementation(project(":mccoroutine-sponge-api"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    testCompileOnly("org.apache.logging.log4j:log4j-api:2.17.2")
    testCompileOnly("it.unimi.dsi:fastutil:7.0.13")
    testCompileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testCompileOnly(files("lib/SpongeCommon.jar"))

    compileOnly("org.spongepowered:spongeapi:7.2.0")
    testCompileOnly("org.spongepowered:spongeapi:7.2.0")
}
