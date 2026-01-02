plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

val publishVersion: String by project
val publishGroupId: String by project

android {
    namespace = "llc.applabs.toolarge.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    api(project(":toolarge-core"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.fragment.ktx)
}

// Ensure the release component exists before publishing config runs
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = publishGroupId
                artifactId = "toolarge-android"
                version = publishVersion

                pom {
                    name.set("TooLarge Android")
                    description.set("Android implementation for TooLarge - Bundle size diagnostics")
                    url.set("https://github.com/myapplabs/TooLarge")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("paulfranco")
                            name.set("Paul Franco")
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
}

