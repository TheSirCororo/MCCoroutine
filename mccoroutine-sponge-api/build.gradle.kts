repositories {
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("org.spongepowered:spongeapi:9.0.0")
    testCompileOnly("org.spongepowered:spongeapi:9.0.0")
}
