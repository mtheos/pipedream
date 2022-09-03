import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("maven-publish")
}

group = "me.theos"
version = "0.1.0-SNAPSHOT"

java {
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.guava:guava:31.1-jre")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "pipedream"
            from(components["kotlin"])
            pom {
                packaging = "jar"
                name.set("pipedream")
                description.set("A library for creating pipes that tee")
                developers {
                    developer {
                        id.set("mtheos")
                        name.set("Michael Theos")
                    }
                }
            }
        }
    }
}
