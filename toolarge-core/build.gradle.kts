plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

val publishVersion: String by project
val publishGroupId: String by project

dependencies {
    implementation(libs.kotlinx.serialization.core)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = publishGroupId
            artifactId = "toolarge-core"
            version = publishVersion
            
            pom {
                name.set("TooLarge Core")
                description.set("Core module for TooLarge - Bundle size diagnostics for Android")
                url.set("https://github.com/myapplabs/TooLarge")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("myapplabs")
                        name.set("ApplLabs LLC")
                        email.set("contact@applabs.llc")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/myapplabs/TooLarge.git")
                    developerConnection.set("scm:git:ssh://github.com:myapplabs/TooLarge.git")
                    url.set("https://github.com/myapplabs/TooLarge")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "central"
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download")
            
            credentials {
                username = findProperty("mavenCentralUsername") as String? ?: findProperty("ossrhUsername") as String?
                password = findProperty("mavenCentralPassword") as String? ?: findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["release"])
}
