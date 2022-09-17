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
    implementation("com.google.guava:guava:31.1-jre")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register("copyPom") {
    description = "Copy pom-default.xml from build into the repo root"
    group = "publishing"
    doLast {
        File("$buildDir/publications/maven/pom-default.xml").copyTo(File("pom.xml"), true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "pipedream"
            from(components["kotlin"])
            pom {
                packaging = "jar"
                scm {
                    url.set("https://github.com/mtheos/pipedream")
                }
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

tasks.getByName("copyPom").dependsOn("generatePomFileForMavenPublication")
tasks.getByName("publishToMavenLocal").dependsOn("copyPom")
