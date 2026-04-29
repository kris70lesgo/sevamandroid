# Sevam Android Local Setup

## Android SDK

Create `local.properties` in the repo root:

```properties
sdk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

Or export one of these before running Gradle:

- `ANDROID_HOME`
- `ANDROID_SDK_ROOT`

## Java

Use Java 17 or Java 21 for Gradle and Kotlin builds. Java 25 currently breaks Kotlin script evaluation in this project.

Example PowerShell session:

```powershell
$env:JAVA_HOME='C:\Users\YOUR_USER\.vscode\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## Supabase env vars

The app reads these from Gradle properties or environment variables:

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SCHEMA`
- `SUPABASE_WORKER_LOCATIONS_TABLE`

## Next verification steps

Once SDK + Java are configured, run:

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat test
```
