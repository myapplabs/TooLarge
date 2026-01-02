# Publishing Guide for TooLarge

This guide explains how to publish the TooLarge library to Maven Central (or other Maven repositories).

## Prerequisites

Before you can publish, you need:

### 1. Maven Central Account (Sonatype OSSRH)
- Sign up at [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa)
- Create a "New Project" ticket to claim your groupId (e.g., `llc.applabs`)
- Wait for approval (usually 1-2 business days)

### 2. GPG Key for Signing
```bash
# Generate a GPG key
gpg --gen-key

# List your keys (note the key ID)
gpg --list-keys

# Upload your public key to a key server
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. Gradle Properties
Create or update `~/.gradle/gradle.properties` with:

```properties
# Sonatype credentials
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

# GPG signing
signing.keyId=YOUR_8_CHAR_KEY_ID
signing.password=YOUR_GPG_PASSWORD
signing.secretKeyRingFile=/path/to/.gnupg/secring.gpg

# Library version
VERSION_NAME=1.0.0
GROUP=llc.applabs.toolarge
```

## Setup Steps

### 1. Complete Publishing Configuration

The library already has basic `maven-publish` setup in `toolarge-android/build.gradle.kts`, but needs completion.

You need to:

1. **Add publishing to toolarge-core** 
2. **Complete POM metadata** (required by Maven Central)
3. **Add signing configuration**
4. **Configure repository URLs**

See the updated build scripts below.

### 2. Update Build Scripts

#### Root `build.gradle.kts`
Add the signing plugin:

```kotlin
plugins {
    // ... existing plugins
    id("signing") apply false
}
```

#### `toolarge-core/build.gradle.kts`
Add complete publishing config:

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

val versionName = findProperty("VERSION_NAME") as String? ?: "1.0.0-SNAPSHOT"
val groupId = findProperty("GROUP") as String? ?: "llc.applabs.toolarge"

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
            groupId = groupId
            artifactId = "toolarge-core"
            version = versionName
            
            pom {
                name.set("TooLarge Core")
                description.set("Core module for TooLarge - Bundle size diagnostics for Android")
                url.set("https://github.com/yourusername/toolarge")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("yourusername")
                        name.set("Your Name")
                        email.set("your.email@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/yourusername/toolarge.git")
                    developerConnection.set("scm:git:ssh://github.com:yourusername/toolarge.git")
                    url.set("https://github.com/yourusername/toolarge")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (versionName.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            
            credentials {
                username = findProperty("ossrhUsername") as String?
                password = findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["release"])
}
```

#### `toolarge-android/build.gradle.kts`
Update the existing publishing block:

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
    id("signing")
}

val versionName = findProperty("VERSION_NAME") as String? ?: "1.0.0-SNAPSHOT"
val groupId = findProperty("GROUP") as String? ?: "llc.applabs.toolarge"

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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = groupId
                artifactId = "toolarge-android"
                version = versionName

                pom {
                    name.set("TooLarge Android")
                    description.set("Android implementation for TooLarge - Bundle size diagnostics")
                    url.set("https://github.com/yourusername/toolarge")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("yourusername")
                            name.set("Your Name")
                            email.set("your.email@example.com")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/yourusername/toolarge.git")
                        developerConnection.set("scm:git:ssh://github.com:yourusername/toolarge.git")
                        url.set("https://github.com/yourusername/toolarge")
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "sonatype"
                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                url = uri(if (versionName.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                
                credentials {
                    username = findProperty("ossrhUsername") as String?
                    password = findProperty("ossrhPassword") as String?
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["release"])
}
```

### 3. Add a License File

Create `LICENSE` file (Apache 2.0 is common for Android libraries):

```
Copyright [YEAR] ApplLabs LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Publishing Process

### Test Locally First

```bash
# Build the library
./gradlew clean build

# Test publishing to local Maven repository
./gradlew publishToMavenLocal

# Check the output in ~/.m2/repository/llc/applabs/toolarge/
```

### Publish SNAPSHOT (for testing)

```bash
# Set version in gradle.properties
VERSION_NAME=1.0.0-SNAPSHOT

# Publish to Sonatype snapshots
./gradlew publishAllPublicationsToSonatypeRepository
```

### Publish Release

```bash
# 1. Update version (remove -SNAPSHOT)
VERSION_NAME=1.0.0

# 2. Publish to Sonatype staging
./gradlew publishAllPublicationsToSonatypeRepository

# 3. Log into Sonatype Nexus
# Visit: https://s01.oss.sonatype.org/

# 4. Close the staging repository
# - Go to "Staging Repositories"
# - Find your repository (llcapplabs-xxxx)
# - Click "Close" button
# - Wait for validation (5-10 minutes)

# 5. Release the repository
# - Click "Release" button
# - Artifacts will sync to Maven Central in 15-30 minutes
```

## Alternative: GitHub Packages

If you want to publish to GitHub Packages instead:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Then:
```bash
./gradlew publish
```

## CI/CD with GitHub Actions

Create `.github/workflows/publish.yml`:

```yaml
name: Publish to Maven Central

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Setup Gradle
        uses: gradle/gradle-build-action@v2
        
      - name: Publish to Maven Central
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
          SIGNING_SECRET_KEY: ${{ secrets.SIGNING_SECRET_KEY }}
        run: |
          echo "$SIGNING_SECRET_KEY" | base64 -d > secring.gpg
          ./gradlew publishAllPublicationsToSonatypeRepository \
            -Possrh.username="$OSSRH_USERNAME" \
            -Possrh.password="$OSSRH_PASSWORD" \
            -Psigning.keyId="$SIGNING_KEY_ID" \
            -Psigning.password="$SIGNING_PASSWORD" \
            -Psigning.secretKeyRingFile=secring.gpg
```

## After Publishing

### Update README Installation Instructions

Once published to Maven Central, users can add:

```kotlin
dependencies {
    implementation("llc.applabs.toolarge:toolarge-android:1.0.0")
}
```

### Create Release Notes

Document what's included in each version:
- New features
- Bug fixes
- Breaking changes
- Migration guide

## Versioning Strategy

Follow [Semantic Versioning](https://semver.org/):
- **MAJOR** (1.0.0): Breaking changes
- **MINOR** (0.1.0): New features, backward compatible
- **PATCH** (0.0.1): Bug fixes

## Checklist Before Publishing

- [ ] All tests pass
- [ ] Documentation is complete
- [ ] LICENSE file exists
- [ ] POM metadata is filled out
- [ ] Version number is correct
- [ ] CHANGELOG is updated
- [ ] Sample app works with published artifact
- [ ] GPG signing is configured
- [ ] Sonatype credentials are set
- [ ] Code is tagged in git (`git tag v1.0.0`)

## Troubleshooting

### "401 Unauthorized" when publishing
- Check your Sonatype credentials in `~/.gradle/gradle.properties`
- Ensure your account has permissions for the groupId

### "No valid OpenPGP data found"
- Ensure your GPG key is properly configured
- Export your secret key: `gpg --export-secret-keys YOUR_KEY_ID > secring.gpg`

### "Component 'release' not found"
- Make sure `afterEvaluate` block is used for Android library
- Check that the variant name matches your build configuration

### Dependencies not resolving after publishing
- Core module must be published before Android module
- Use `./gradlew :toolarge-core:publish` then `./gradlew :toolarge-android:publish`

## Resources

- [Maven Central Guide](https://central.sonatype.org/publish/publish-guide/)
- [Gradle Publishing](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Android Library Publishing](https://developer.android.com/studio/build/maven-publish-plugin)
