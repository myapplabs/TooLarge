# Publishing Guide for TooLarge Library

## Current Status

✅ **GPG Signing**: Fixed and working  
❌ **Maven Central Publishing**: Requires namespace verification

## Issue

Maven Central returned: `401 Content access is protected by token`

This means you need to verify ownership of the `io.github.myapplabs` namespace on Maven Central.

## Solution: Complete Maven Central Setup

### Step 1: Verify GitHub Namespace Ownership

1. Go to [https://central.sonatype.com/](https://central.sonatype.com/)
2. Sign in or create an account
3. Go to "Namespaces"  
4. Click "Add Namespace"
5. Enter: `io.github.myapplabs`
6. Verify ownership by:
   - **Option A**: Create a verification repository on GitHub
   - **Option B**: Add a verification TXT record to your GitHub Pages DNS

### Step 2: Generate User Token

After namespace verification:

1. In Maven Central Portal, go to "Account" → "Generate User Token"
2. Copy the **username** and **password** (these are NOT your Sonatype login credentials)
3. Update your GitHub secrets:
   ```bash
   gh secret set MAVEN_CENTRAL_USERNAME
   # Paste the token username when prompted
   
   gh secret set MAVEN_CENTRAL_PASSWORD  
   # Paste the token password when prompted
   ```

### Step 3: Re-run the Publish Workflow

Once secrets are updated:

```bash
gh workflow run "Publish to Maven Central" --field version=1.0.0
```

## Alternative: Manual Publishing

If the automated workflow still has issues, you can publish manually:

###  1. Build and Sign Locally

```bash
./gradlew publishToMavenLocal \
  -PpublishVersion=1.0.0 \
  -PmavenCentralUsername="YOUR_TOKEN_USERNAME" \
  -PmavenCentralPassword="YOUR_TOKEN_PASSWORD" \
  -Psigning.keyId="1AF32819" \
  -Psigning.password="YOUR_GPG_PASSWORD" \
  -Psigning.secretKeyRingFile=$HOME/.gnupg/secring.gpg
```

### 2. Create Bundle

```bash
cd ~/.m2/repository/io/github/myapplabs
zip -r ~/toolarge-bundle.zip toolarge-core toolarge-android
```

### 3. Upload to Maven Central Portal

1. Go to [https://central.sonatype.com/publishing](https://central.sonatype.com/publishing)
2. Click "Upload Bundle"
3. Select `toolarge-bundle.zip`
4. Click "Publish"

## Quick Test: Use Maven Local

While setting up Maven Central, you and others can use Maven Local:

### In This Project
```bash
./gradlew publishToMavenLocal -PpublishVersion=1.0.0
```

### In Other Projects

**settings.gradle.kts:**
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

**build.gradle.kts:**
```kotlin
dependencies {
    implementation("io.github.myapplabs:toolarge-android:1.0.0")
}
```

## Resources

- [Maven Central Publishing Requirements](https://central.sonatype.org/publish/requirements/)
- [Namespace Verification](https://central.sonatype.org/publish/publish-portal-namespace/)  
- [Generate User Token](https://central.sonatype.org/publish/generate-token/)

