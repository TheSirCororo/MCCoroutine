repositories {
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    implementation(project(":mccoroutine-sponge-api"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testCompileOnly("org.apache.logging.log4j:log4j-api:2.19.0")
    testCompileOnly("it.unimi.dsi:fastutil:8.5.8")
    testCompileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testCompileOnly(files("lib/SpongeCommon.jar"))

    compileOnly("org.spongepowered:spongeapi:9.0.0")
    testCompileOnly("org.spongepowered:spongeapi:9.0.0")
}
