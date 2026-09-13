# Marti Location Tracker Case

Android location tracking case project built with Kotlin and Jetpack Compose. The app displays the user's current location on Google Maps, tracks movement in the foreground/background, stores route points locally, and visualizes the traveled path on the map.

## Features

- Shows the user's current location on Google Maps
- Starts, stops, and resets location tracking
- Tracks location updates with a foreground service
- Keeps tracking while the app is in the background
- Stores route points locally with Room
- Draws the traveled route on the map
- Adds marker points based on movement distance
- Resolves marker addresses with Google Geocoding API
- Uses Google Roads API to improve route line accuracy
- Separates UI state, actions, ViewModel, domain use cases, repositories, and data sources

## Tech Stack

- Kotlin
- MVVM
- Jetpack Compose
- Google Maps
- Google Play Services Location
- Foreground Service
- Room
- Retrofit
- Hilt
- Coroutines and Flow
- JUnit, MockK, Coroutines Test

## Architecture

The project follows a clean architecture-oriented structure:

```text
app
├── data
│   ├── local
│   ├── remote
│   ├── repository
│   ├── service
│   └── tracking
├── di
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── presentation
│   ├── common
│   └── tracking
└── ui
    └── theme
```

- `presentation`: Compose screens, UI state, UI actions, ViewModel, previews, and UI helpers
- `domain`: Models, repository contracts, and use cases
- `data`: Repository implementations, Room database, remote API contracts, foreground service, and in-memory tracking state
- `di`: Hilt modules for dependency injection

## Setup

1. Clone the repository.
2. Open the project in Android Studio.
3. Add your Google Maps API key to `local.properties`:

```properties
MAPS_API_KEY=YOUR_API_KEY
```

4. Make sure the following Google Cloud APIs are enabled for the key:

- Maps SDK for Android
- Roads API
- Geocoding API

5. Sync Gradle and run the app.

`local.properties` is ignored by Git, so API keys are not committed to the repository.

## Permissions

The app uses location and foreground service permissions:

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `ACCESS_BACKGROUND_LOCATION`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_LOCATION`
- `POST_NOTIFICATIONS`

Notification permission is required on Android 13+ because tracking runs as a foreground service.

## Testing

Run unit tests with:

```bash
./gradlew test
```

Run debug Kotlin compilation with:

```bash
./gradlew :app:compileDebugKotlin
```

## AI Assistance

AI assistance was used during development for debugging support, refactoring suggestions, and reviewing selected implementation details. A summary is available in [AI_USAGE.md](AI_USAGE.md).
