# GitHub Actions Publishing Setup

This repository is configured to automatically publish to Maven Central using GitHub Actions.

## One-Time Setup: Configure GitHub Secrets

Go to your GitHub repository settings and add these secrets:

### 1. Navigate to Secrets
- Go to: https://github.com/myapplabs/TooLarge/settings/secrets/actions
- Click "New repository secret" for each of the following:

### 2. Add These Secrets

| Secret Name | Value | Where to Find |
|-------------|-------|---------------|
| `MAVEN_CENTRAL_USERNAME` | `tHKl47` | Your Maven Central username (already have it) |
| `MAVEN_CENTRAL_PASSWORD` | `GpjgGHlZdtkC9Oe2sTaXJ2VMLxMkr2OQQ` | Your Maven Central password (already have it) |
| `SIGNING_KEY_ID` | `1AF32819` | Your GPG key ID (already have it) |
| `SIGNING_PASSWORD` | `MasterMason2025!` | Your GPG key password (already have it) |
| `GPG_KEY_BASE64` | [See below] | Your GPG private key in base64 format |

### 3. Get GPG_KEY_BASE64 Value

Run this command and copy the entire output:

```bash
cat /tmp/gpg-key-base64.txt
```

Copy the entire string and paste it as the value for `GPG_KEY_BASE64` secret.

## How to Publish a New Version

### Option 1: Create a GitHub Release (Recommended)

1. Go to: https://github.com/myapplabs/TooLarge/releases/new
2. Create a new tag: `v1.0.0` (or your version number)
3. Set release title: `v1.0.0`
4. Add release notes
5. Click "Publish release"
6. The workflow will automatically run and publish to Maven Central

### Option 2: Manual Trigger

1. Go to: https://github.com/myapplabs/TooLarge/actions/workflows/publish.yml
2. Click "Run workflow"
3. Enter the version number (e.g., `1.0.0`)
4. Click "Run workflow"

## After Publishing

The workflow will:
1. ✅ Build both library modules
2. ✅ Sign all artifacts with your GPG key
3. ✅ Create a bundle with all required files
4. ✅ Upload to Maven Central Portal
5. ✅ Automatically publish (make available on Maven Central)

Library will be available at:
- `io.github.myapplabs:toolarge-core:VERSION`
- `io.github.myapplabs:toolarge-android:VERSION`

It typically takes 15-30 minutes for artifacts to sync to Maven Central after publishing.

## Troubleshooting

### Workflow fails with authentication error
- Verify all secrets are set correctly in GitHub
- Check that your Maven Central credentials haven't expired

### GPG signing fails
- Ensure `GPG_KEY_BASE64` is correctly formatted (no extra newlines)
- Verify `SIGNING_PASSWORD` matches your GPG key password

### Build fails
- Check the workflow logs in the Actions tab
- Ensure `publishVersion` in gradle.properties matches the release tag

## Updating Version for Next Release

Before creating a new release, update the version in `gradle.properties`:

```properties
publishVersion=1.1.0
```

Then commit and push the change before creating the release.
