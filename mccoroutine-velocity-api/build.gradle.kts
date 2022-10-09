repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    testCompileOnly("com.velocitypowered:velocity-api:3.1.1")
}
