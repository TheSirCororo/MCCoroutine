import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("kapt") version "1.7.10" apply false
    id("io.codearte.nexus-staging") version "0.30.0"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    id("org.jetbrains.dokka") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    signing
}

allprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

nexusStaging {
    packageGroup = "com.github.shynixn"
    username =
        if (project.hasProperty("ossrhUsername")) project.findProperty("ossrhUsername") as? String else System.getenv("SONATYPE_USERNAME")
            ?: ""
    password =
        if (project.hasProperty("ossrhPassword")) project.findProperty("ossrhPassword") as? String else System.getenv("SONATYPE_PASSWORD")
            ?: ""
    delayBetweenRetriesInMillis = 10000
    numberOfRetries = 100
}

tasks.register("printVersion") {
    println(subprojects.first().version)
}

subprojects {
    group = "com.github.shynixn.mccoroutine"
    version = "2.5.0"

    apply(plugin = "kotlin")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "de.marcphilipp.nexus-publish")

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        compileJava {
            sourceCompatibility = "1.8"
        }

        test {
            useJUnitPlatform()
            testLogging.showStandardStreams = true
            failFast = true

            testLogging {
                events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.STARTED)
                displayGranularity = 0
                showExceptions = true
                showCauses = true
                showStackTraces = true
                exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }

    val sourcesJar = tasks.create<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }

    val dokkaTask = tasks.named<DokkaTask>("dokkaHtml") {
        outputDirectory.set(file("$buildDir/javadoc"))
    }

    val javadocJar = tasks.create<Jar>("javadocJar") {
        dependsOn(dokkaTask.get())
        from(dokkaTask.get().outputDirectory)
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
                artifact(sourcesJar)
                artifact(javadocJar)
                pom {
                    name.set("MCCoroutine")
                    description.set("MCCoroutine is a library, which adds extensive support for Kotlin Coroutines for Minecraft Server environments.")
                    url.set("https://github.com/Shynixn/MCCoroutine")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("http://www.opensource.org/licenses/mit-license.php")
                        }
                    }
                    developers {
                        developer {
                            name.set("Shynixn")
                            url.set("https://github.com/Shynixn")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/Shynixn/MCCoroutine.git")
                        developerConnection.set("scm:git:ssh://github.com:Shynixn/MCCoroutine.git")
                        url.set("http://github.com/Shynixn/MCCoroutine.git/tree/master")
                    }
                }
            }
            repositories {
                maven {
                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                    url = if ((version as String).endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)

                    credentials {
                        username =
                            if (project.hasProperty("ossrhUsername")) project.findProperty("ossrhUsername") as? String else System.getenv(
                                "SONATYPE_USERNAME"
                            ) ?: ""
                        password =
                            if (project.hasProperty("ossrhPassword")) project.findProperty("ossrhPassword") as? String else System.getenv(
                                "SONATYPE_PASSWORD"
                            ) ?: ""
                    }
                }
            }
        }

        signing {
            sign(publishing.publications["mavenJava"])
        }
    }

    dependencies {
        testImplementation(kotlin("kotlin-test"))
        testImplementation(kotlin("kotlin-test-junit"))
        testImplementation("org.mockito:mockito-core:4.8.0")
    }
}

gradle.taskGraph.whenReady {
    if (project.findProperty("signing.keyId") == null) {
        ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        ext["signing.password"] = System.getenv("SIGNING_KEY_PASSWORD")
        ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_KEY_FILE")
    }
}

tasks.create<DokkaTask>("generateBukkitJavaDocPages") {
    dokkaSourceSets {
        named("main") {
            outputDirectory.set(file("docs/apidocs"))
            sourceRoots.from(file("mccoroutine-bukkit-api/src/main/java"))
        }
    }
}

tasks.create<DokkaTask>("generateSpongeJavaDocPages") {
    dokkaSourceSets {
        named("main") {
            outputDirectory.set(file("docs/apidocs"))
            sourceRoots.from(file("mccoroutine-sponge-api/src/main/java"))
        }
    }
}

tasks.create<DokkaTask>("generateBungeeCordJavaDocPages") {
    dokkaSourceSets {
        named("main") {
            outputDirectory.set(file("docs/apidocs"))
            sourceRoots.from(file("mccoroutine-bungeecord-api/src/main/java"))
        }
    }
}

tasks.create<DokkaTask>("generateVelocityJavaDocPages") {
    dokkaSourceSets {
        named("main") {
            outputDirectory.set(file("docs/apidocs"))
            sourceRoots.from(file("mccoroutine-velocity-api/src/main/java"))
        }
    }
}
