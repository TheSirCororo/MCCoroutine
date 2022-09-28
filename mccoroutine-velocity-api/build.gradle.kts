repositories {
    maven("https://nexus.velocitypowered.com/repository/maven-public")
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    testCompileOnly("com.velocitypowered:velocity-api:3.0.1")
}
