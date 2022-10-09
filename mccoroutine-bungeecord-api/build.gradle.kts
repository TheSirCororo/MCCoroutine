repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    testCompileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
}
