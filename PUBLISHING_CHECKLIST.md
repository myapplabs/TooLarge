# Publishing Checklist

Quick reference checklist for publishing TooLarge to Maven Central.

## One-Time Setup

### 1. Maven Central Account
- [ ] Sign up at [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa)
- [ ] Create "New Project" ticket for `llc.applabs` groupId
- [ ] Wait for approval (typically 1-2 business days)

### 2. GPG Key Setup
```bash
# Generate key
gpg --gen-key

# Export public key to key server
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export secret key (for local use)
gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg
```

### 3. Configure Gradle Properties
Add to `~/.gradle/gradle.properties`:
```properties
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password
signing.keyId=YOUR_8_CHAR_KEY_ID
signing.password=YOUR_GPG_PASSWORD
signing.secretKeyRingFile=/Users/username/.gnupg/secring.gpg
```

### 4. Update Repository URLs
- [ ] Replace GitHub URLs in build.gradle.kts files with your actual repository
- [ ] Update developer info in POM configurations
- [ ] Update email addresses

## Pre-Release Checklist

### Code Quality
- [ ] All tests pass: `./gradlew test`
- [ ] No linter errors: `./gradlew lint`
- [ ] Sample app builds and runs correctly
- [ ] Documentation is up to date

### Version Management
- [ ] Update `VERSION_NAME` in `gradle.properties` (remove `-SNAPSHOT` for release)
- [ ] Update version in README installation example
- [ ] Update CHANGELOG.md with release notes
- [ ] Commit all changes

### Git Tagging
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## Publishing Steps

### 1. Test Local Build
```bash
./gradlew clean build
./gradlew publishToMavenLocal
# Verify: ls ~/.m2/repository/llc/applabs/toolarge/
```

### 2. Publish to Sonatype
```bash
# Publish both modules
./gradlew publishAllPublicationsToMavenLocal

# If publishing to Sonatype is configured:
# ./gradlew publishAllPublicationsToSonatypeRepository
```

### 3. Release via Sonatype Nexus
1. Log into [Sonatype Nexus](https://s01.oss.sonatype.org/)
2. Go to "Staging Repositories"
3. Find your staging repository (e.g., `llcapplabs-1001`)
4. Click "Close" button
5. Wait for validation (5-10 minutes)
6. Check validation results
7. Click "Release" button
8. Wait for sync to Maven Central (15-30 minutes)

### 4. Verify Publication
- [ ] Check Maven Central search: https://search.maven.org/
- [ ] Search for: `llc.applabs.toolarge:toolarge-android`
- [ ] Verify both modules are published
- [ ] Check POM files are correct

## Post-Release

### Documentation Updates
- [ ] Update README with new version number
- [ ] Create GitHub release with changelog
- [ ] Update CHANGELOG.md for next version
- [ ] Announce on social media / blog (optional)

### Prepare Next Version
- [ ] Bump version to next SNAPSHOT: e.g., `1.1.0-SNAPSHOT`
- [ ] Commit version bump
- [ ] Push to repository

## Quick Commands Reference

```bash
# Clean build
./gradlew clean build

# Publish to local Maven (~/.m2/repository)
./gradlew publishToMavenLocal

# Publish to Sonatype (requires credentials)
./gradlew publishAllPublicationsToSonatypeRepository

# Check what will be published
./gradlew tasks --group publishing

# Generate sources and javadoc JARs
./gradlew androidSourcesJar
./gradlew androidJavadocsJar
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Check Sonatype credentials in `~/.gradle/gradle.properties` |
| Signing failed | Verify GPG key ID and password are correct |
| Component not found | Use `afterEvaluate` block for Android modules |
| POM validation failed | Ensure all required POM fields are filled |
| Repository not found | Check Sonatype URL (s01 vs s02) |

## Alternative: GitHub Packages

If publishing to GitHub Packages instead:

1. Add to `~/.gradle/gradle.properties`:
```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

2. Update repository in build.gradle.kts:
```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/OWNER/REPO")
        credentials {
            username = findProperty("gpr.user") as String?
            password = findProperty("gpr.key") as String?
        }
    }
}
```

3. Publish:
```bash
./gradlew publish
```

## Resources

- **Maven Central Guide**: https://central.sonatype.org/publish/
- **Gradle Publishing**: https://docs.gradle.org/current/userguide/publishing_maven.html
- **Android Publishing**: https://developer.android.com/build/publish-library

---

For detailed explanations, see [PUBLISHING.md](PUBLISHING.md)
