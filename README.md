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
