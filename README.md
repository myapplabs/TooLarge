# TooLarge

A lightweight Android library that helps you detect and debug **TransactionTooLargeException** issues before they crash your app.

## What Problem Does This Solve?

Android has a strict **1MB limit** on data passed through `Bundle` objects (used in Activities, Fragments, and saved instance states). When you exceed this limit, your app crashes with a `TransactionTooLargeException`. This library:

- 🔍 **Monitors** Bundle sizes across Activities and Fragments automatically
- 📊 **Reports** which specific data is consuming the most space
- ⚠️ **Warns** you before reaching the 1MB limit
- 🐛 **Helps debug** by pinpointing the exact offenders

## Installation

### Step 1: Add Maven Central repository

Make sure Maven Central is added to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // Make sure this is included
    }
}
```

### Step 2: Add the dependency

Add the library to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.myapplabs:toolarge-android:1.0.0")
}
```

### Step 3: Import and initialize in your Application class

```kotlin
import android.app.Application
import llc.applabs.toolarge.android.TooLarge
import llc.applabs.toolarge.android.LogcatSink
import llc.applabs.toolarge.model.Origin
import llc.applabs.toolarge.policy.Budget
import llc.applabs.toolarge.policy.PolicyConfig

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val policy = PolicyConfig(
            budgets = mapOf(
                Origin.ACTIVITY_SAVE_INSTANCE_STATE to Budget(200_000, 500_000),
                Origin.ACTIVITY_INTENT_EXTRAS to Budget(50_000, 200_000),
                Origin.FRAGMENT_ARGUMENTS to Budget(50_000, 200_000),
                Origin.FRAGMENT_SAVE_INSTANCE_STATE to Budget(100_000, 300_000),
            )
        )
        
        TooLarge.install(
            this,
            TooLarge.AndroidConfig(
                policy = policy,
                sink = LogcatSink(tag = "TooLarge"),
                debug = BuildConfig.DEBUG
            )
        )
    }
}
```

## Usage

Once installed, TooLarge **works automatically**. It monitors:

- **Activity Intent Extras** - Data passed via `Intent.putExtra()`
- **Activity Saved Instance State** - Data saved in `onSaveInstanceState()`
- **Fragment Arguments** - Data passed via `Fragment.setArguments()`
- **Fragment Saved Instance State** - Data saved in Fragment's `onSaveInstanceState()`

### Reading the Logs

When a Bundle exceeds your configured thresholds, you'll see detailed logs in Logcat:

```
W/TooLarge: TOOLARGE origin=ACTIVITY_SAVE_INSTANCE_STATE owner=com.example.MainActivity total=520000 bytes
W/TooLarge:   #0 220000B  activity_big  (byte[]) 
W/TooLarge:   #1 180000B  android:fragments  (Bundle)
W/TooLarge:   #2 120000B  some_data  (String)
```

This tells you:
- **origin**: Where the large Bundle came from
- **owner**: Which Activity/Fragment is responsible
- **total**: Total size in bytes
- **Offenders**: Top items consuming space, ordered by size

## Configuration

### Budget Configuration

Configure different size limits for different Bundle origins:

```kotlin
val budget = Budget(
    warnBytes = 200_000,    // Log a warning at 200KB
    errorBytes = 500_000,   // Log an error at 500KB
    action = Budget.Action.LOG  // or Budget.Action.THROW_IN_DEBUG
)
```

**Available Origins:**
- `ACTIVITY_SAVE_INSTANCE_STATE` - Activity state restoration
- `ACTIVITY_INTENT_EXTRAS` - Intent extras passed to Activities
- `FRAGMENT_SAVE_INSTANCE_STATE` - Fragment state restoration
- `FRAGMENT_RESTORED_INSTANCE_STATE` - Fragment instance state when restored
- `FRAGMENT_ARGUMENTS` - Fragment arguments
- `INTENT_EXTRAS` - Generic intent extras
- `NAV_BACK_STACK` - Navigation back stack entries
- `SAVED_STATE_REGISTRY` - Saved state registry entries

### Policy Options

```kotlin
PolicyConfig(
    budgets = mapOf(/* your budgets */),
    topN = 20,                      // Report top 20 offenders
    includeStacktrace = true        // Include stacktrace in reports
)
```

### Budget Actions

- `Budget.Action.LOG` - Only log the violation (default)
- `Budget.Action.THROW_IN_DEBUG` - Throw an exception in debug builds

### Custom Sinks

Implement `ReportSink` to send reports anywhere:

```kotlin
import llc.applabs.toolarge.model.TooLargeReport
import llc.applabs.toolarge.sink.ReportSink

class CustomSink : ReportSink {
    override fun emit(report: TooLargeReport) {
        // Send to your analytics service
        // Send to crash reporting
        // Display in your debug UI
    }
}
```

Built-in sinks:
- `LogcatSink` - Logs to Android Logcat
- `CompositeSink` - Combine multiple sinks

## Testing

Use the self-test function to verify your configuration:

```kotlin
TooLarge.selfTest(
    app = this,
    cfg = TooLarge.AndroidConfig(
        policy = yourPolicy,
        sink = yourSink,
        debug = true
    )
)
```

## Best Practices

### Recommended Budget Limits

Based on Android's 1MB transaction limit:

```kotlin
PolicyConfig(
    budgets = mapOf(
        // Conservative limits - good for most apps
        Origin.ACTIVITY_SAVE_INSTANCE_STATE to Budget(200_000, 500_000),
        Origin.ACTIVITY_INTENT_EXTRAS to Budget(50_000, 200_000),
        Origin.FRAGMENT_ARGUMENTS to Budget(50_000, 200_000),
        Origin.FRAGMENT_SAVE_INSTANCE_STATE to Budget(100_000, 300_000),
    )
)
```

### What to Do When You Get Warnings

1. **Don't pass large data through Bundles** - Use:
   - ViewModel for UI state
   - Database for large datasets
   - File storage for images/files
   - Singleton repositories for shared data

2. **Reduce what you save** - Only save essential UI state:
   ```kotlin
   override fun onSaveInstanceState(outState: Bundle) {
       super.onSaveInstanceState(outState)
       // ❌ Don't save entire user objects or large lists
       // ✅ Save IDs and reload from database
       outState.putString("user_id", currentUserId)
   }
   ```

3. **Use Parcelable efficiently** - Avoid nested Parcelables and large collections

## Architecture

The library consists of two modules:

- **toolarge-core** - Platform-agnostic core (policies, models, sinks)
- **toolarge-android** - Android-specific implementation (Bundle analysis, Activity/Fragment hooks)

## Requirements

- **Android API 21+** (Android 5.0 Lollipop)
- **Kotlin 2.0.21+**
- **AndroidX Fragment 1.8.5+**

## Sample App

Check out the `:app` module for a complete example showing:
- How to configure TooLarge
- Intentionally oversized Bundles to trigger warnings
- How to read and interpret the logs

## Troubleshooting

### Unresolved Reference Errors

If you're getting "unresolved reference" errors for `PolicyConfig`, `Origin`, `TooLarge`, etc.:

1. **Verify Maven coordinates** - Make sure you're using:
   ```kotlin
   implementation("io.github.myapplabs:toolarge-android:1.0.0")
   ```
   NOT `llc.applabs.toolarge:toolarge-android:1.0.0`

2. **Check repository configuration** - Ensure Maven Central is in your `settings.gradle.kts`:
   ```kotlin
   repositories {
       google()
       mavenCentral()
   }
   ```

3. **Use correct imports** - The package names use `llc.applabs.toolarge.*`:
   ```kotlin
   import llc.applabs.toolarge.android.TooLarge
   import llc.applabs.toolarge.model.Origin
   import llc.applabs.toolarge.policy.PolicyConfig
   ```

4. **Sync your project** - After adding the dependency, sync Gradle:
   - In Android Studio: File → Sync Project with Gradle Files
   - Or run: `./gradlew --refresh-dependencies`

5. **Check your Gradle version** - This library requires Gradle 7.0+ and AGP 8.0+

6. **Clean and rebuild**:
   ```bash
   ./gradlew clean build
   ```

### Library Not Found in Maven Central

If Maven Central can't find the library, it may not be published yet or the publication may have failed. 

**To verify publication status:**

1. Check Maven Central:
   - Search: `https://search.maven.org/search?q=g:io.github.myapplabs+AND+a:toolarge-android`
   - Direct: `https://central.sonatype.com/artifact/io.github.myapplabs/toolarge-android`

2. Check GitHub releases:
   - Visit: `https://github.com/myapplabs/TooLarge/releases`
   - Ensure v1.0.0 is published and the workflow succeeded

3. **Temporary workaround - Use Maven Local** (for testing or if not yet published):
   - Clone this repository
   - Run: `./gradlew publishToMavenLocal -PpublishVersion=1.0.0`
   - Add to your project's `settings.gradle.kts`:
     ```kotlin
     dependencyResolutionManagement {
         repositories {
             mavenLocal()  // Add this BEFORE mavenCentral()
             google()
             mavenCentral()
         }
     }
     ```
   - Use the dependency: `implementation("io.github.myapplabs:toolarge-android:1.0.0")`

## License

```
Copyright 2026 ApplLabs LLC

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

See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development

1. Clone the repository
2. Open in Android Studio
3. Run the sample app to see the library in action
4. Make your changes
5. Ensure all tests pass
6. Submit a PR

## Credits

Developed by ApplLabs LLC

---

**Found this useful?** Give it a ⭐️ to show your support!
