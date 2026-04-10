# Sevam Core Architecture (Target)

This document captures the target module boundaries for the Sevam customer Android app.

## Root

```text
sevam-customer-android/
├── app/
├── build-logic/
├── gradle/
├── core/
├── features/
├── docs/
├── config/
└── build.gradle.kts
```

## app (Customer shell)

- Hosts `Application`, `MainActivity`, and root navigation.
- Depends on `core:*` and `features:*` modules.
- Includes launcher icon and network security config.

## build-logic (Convention plugins)

- Centralizes Gradle conventions for app, library, feature, Compose, and quality.
- Keeps module build files minimal and consistent.

## core (Shared cross-feature modules)

- `core/common`: dispatchers, `Result`, extensions, formatters.
- `core/network`: Retrofit, OkHttp, interceptors, API services.
- `core/database`: Room database, entities, DAOs.
- `core/security`: encrypted token and crypto helpers.
- `core/location`: Mapbox setup, fused location abstractions, geocoding.
- `core/payments`: Razorpay abstraction and callbacks.
- `core/notifications`: Supabase-backed realtime/push routing abstraction.
- `core/ui`: shared Compose theme and reusable components.

## features (Customer feature modules)

- `features/auth`: phone OTP login and verification.
- `features/home`: categories and booking flow.
- `features/bookings`: active/history bookings and detail.
- `features/tracking`: realtime worker location tracking.
- `features/payments`: order creation and payment handling.
- `features/profile`: profile and saved addresses.

## Backend and Realtime Direction

The project direction is Supabase-first instead of Firebase.

- Realtime updates: Supabase Realtime channels.
- Push notification orchestration: Supabase Edge Functions + backend integration.
- Analytics and crash pipeline: backend/Supabase observability path.
- Authentication: custom JWT from Sevam backend.

## Migration Notes

- Existing sample package names (`com.example.android.architecture.blueprints.*`) remain in code for now.
- Initial migration should prioritize module extraction in this order:
  1. `core/common`, `core/network`, `core/security`
  2. `features/auth`, `features/home`
  3. `core/database`, `core/location`, `features/tracking`
  4. remaining feature modules
