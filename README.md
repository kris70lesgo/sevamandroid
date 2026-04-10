# Sevam Customer Android

Android customer application for Sevam.

This repository currently starts from the Android Architecture sample baseline and is being migrated to Sevam's production structure.

## Kotlin Android Tech Stack

### Core Architecture

| Component | Choice | Why |
| --- | --- | --- |
| Language | Kotlin 2.0+ | Native Android, null-safety, coroutines |
| UI Framework | Jetpack Compose | Modern declarative UI, fast iteration |
| Architecture | MVVM + Clean Architecture | Testable and modular boundaries |
| Build System | Gradle (Kotlin DSL) | Type-safe build configuration |

### Networking and Data

| Library | Purpose |
| --- | --- |
| Retrofit 2 | REST API calls to backend |
| OkHttp | HTTP client with JWT interceptors |
| Kotlinx Serialization | Kotlin-native JSON parsing |
| Room | Local SQLite storage for offline history |
| Datastore | Key-value storage replacement for SharedPreferences |
| EncryptedSharedPreferences | Secure JWT storage |

### Async and State

| Library | Purpose |
| --- | --- |
| Coroutines | Background work for network and database |
| Flow/StateFlow | Reactive updates for Compose UI |
| WorkManager | Background jobs and location sync |
| Hilt | Dependency injection |

### UI and Media

| Library | Purpose |
| --- | --- |
| Coil | Image loading with Compose support |
| Accompanist | Compose utility support |
| Material 3 | Design system components |
| CameraX | Capture job completion photos |

### Feature SDKs

| Feature | SDK | Notes |
| --- | --- | --- |
| Maps | Mapbox Android SDK | Preferred over Google Maps |
| Location | Google Play Services Location | Fused location provider |
| Payments | Razorpay Android SDK | UPI, cards, wallets |
| Realtime + Push + Analytics + Crash Reporting | Supabase | Supabase Realtime + Postgres + Edge Functions + observability pipeline |
| Auth | Custom JWT (backend) | Token stored in encrypted preferences |

## Planned Project Layout

Target structure for the Sevam migration is documented in [docs/architecture/sevam-core-architecture.md](docs/architecture/sevam-core-architecture.md).

## Current Status

- App launcher name is set to Sevam.
- Repository keeps existing modules (`app`, `shared-test`) while migration to multi-module layout is in progress.

## Build

```bash
./gradlew build
```

## Local Setup For Contributors

Use this checklist before you start development.

### 1. Install Required Tools

- Git (latest stable)
- Android Studio (latest stable, with Android SDK tools)
- JDK 17 (required by this project)
- Android SDK Platform 35
- Android SDK Build-Tools 35.x
- Android SDK Platform-Tools (adb)

Optional but recommended:

- Android Emulator with a recent Pixel image (API 34 or 35)

### 2. Clone The Repository

```bash
git clone <your-repo-url>
cd sevamandroid
```

### 3. Verify Java And Android Tooling

```bash
java -version
./gradlew --version
adb version
```

You should see Java 17 and Gradle running successfully.

### 4. Open In Android Studio

- Open the repository root folder.
- Let Gradle sync finish.
- If prompted, use Gradle JDK 17.

### 5. Configure Local Properties

Create or update local.properties in the project root if needed:

```properties
sdk.dir=/Users/<your-username>/Library/Android/sdk
```

Do not commit local.properties.

### 6. Configure Supabase For Local Development

Current defaults are empty in app BuildConfig, so app features that need backend keys should be configured per developer machine.

Minimum values to set before backend-integrated testing:

- SUPABASE_URL
- SUPABASE_ANON_KEY
- SUPABASE_SCHEMA (default: public)
- SUPABASE_WORKER_LOCATIONS_TABLE (default: worker_locations)

Recommended approach:

- Keep real secrets out of git.
- Use local-only Gradle properties or environment variables and map them in app/build.gradle.kts.

### 7. First Build

```bash
./gradlew :app:assembleDebug
```

### 8. Run Tests

```bash
./gradlew test
./gradlew connectedDebugAndroidTest
```

Note: connectedDebugAndroidTest requires a running emulator or connected device.

### 9. Common Troubleshooting

- Gradle sync fails: confirm JDK 17 and SDK 35 are installed.
- ADB not found: install Android Platform-Tools and ensure adb is on PATH.
- Build cache issues: run ./gradlew clean and re-sync.
- Dependency resolution issues: check internet access and Gradle proxy settings.
