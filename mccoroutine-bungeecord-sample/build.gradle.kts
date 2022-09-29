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
    // destinationDir = File("C:\\temp\\BungeeCord\\plugins")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(project(":mccoroutine-bungeecord-api"))
    implementation(project(":mccoroutine-bungeecord-core"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    compileOnly("net.md-5:bungeecord-api:1.16-R0.5-SNAPSHOT")
    testCompileOnly("net.md-5:bungeecord-api:1.16-R0.5-SNAPSHOT")
}
