apply(plugin = "kotlin")

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    version = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":mccoroutine-velocity-api"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("org.apache.logging.log4j:log4j-core:2.19.0")

    testCompileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testCompileOnly(files("lib/velocity.jar"))
    testCompileOnly("com.velocitypowered:velocity-api:3.1.1")
}
