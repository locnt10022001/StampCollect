# Stamp Collection App (StampCollect) - Architecture & Context

## 1. Project Overview
- **Name**: StampCollect
- **Domain**: Stamp Collection application.
- **Goal**: Capture real-world stamps via a custom camera frame, auto-crop with perforated border, manage in collections with a scrapbook layout and calendar tracking.
- **Tech Stack**: Kotlin, Jetpack Compose, CameraX, Room, Hilt, Coil.

## 2. Architecture (MVVM & State Management)

### Data Layer (`com.stampcollect.data`)
- **StampDatabase** — Room, Version 3 with destructive migration.
- **Entities**:
  - `CollectionEntity` (id, name, description)
  - `StampEntity` (id, collectionId, imagePath, name, description, offsetX, offsetY, rotation, zIndex, timestamp)
- **DAOs**: `CollectionDao`, `StampDao` (supports insert, update, getAll, getByCollection)
- **Repository**: `StampRepository` — wraps DAOs and returns data cleanly via standard data structures or as part of advanced MVVM flows.

### Dependency Injection (`com.stampcollect.di`)
- `AppModule` — provides Database (with `fallbackToDestructiveMigration()`), DAOs, Repository as Singletons.

### Core Architecture & State Management (`com.stampcollect.util` & `com.stampcollect.ui.viewmodel`)
- **Resource Wrapper**: `Resource<T>` sealed class handles UI states: `Loading`, `Success`, `Error`.
- **BaseViewModel**: Encapsulates common state definitions (`MutableStateFlow<Resource<*>>`) and safe `viewModelScope.launch` helpers.
- **Security & Cryptography (`com.stampcollect.util`)**:
  - `EncryptUtil`: Uses AndroidX `security-crypto` (EncryptedSharedPreferences) for secure config storage.
  - `BiometricKeystoreManager`: Implements robust biometric authentication (App Lock) protecting the user's private stamp collections.
  
### UI Layer (`com.stampcollect.ui`)

#### Theme (`ui/theme/`)
- **Color.kt** — High-contrast, minimal iOS-style palette: Primary (deep blue #1565C0), Accent (deep orange), clean BgPrimary/BgCard whites, TextPrimary/Secondary grays for sharp legibility. Includes warm **StampPaper** and **StampPaperGrid** tones for authentic collection backings.
- **Type.kt** — Compact typography scale, SemiBold-to-Normal hierarchy.
- **Theme.kt** — Light `MaterialTheme` color scheme with boosted contrast.

#### ViewModel
- `CollectionViewModel` — Extends `BaseViewModel`. Provides flows wrapped in `Resource` states for `currentCollectionStamps`.
- Auto-seeds a default "All" collection on first launch and gracefully manages state transitions.

#### Navigation (`ui/navigation/`)
- `AppNavigation.kt` — NavHost with routes: `main` (MainScreen) → `detail/{id}/{name}` (CollectionDetailScreen) → `day/{date}` (DayStampsScreen).

#### Screens (`ui/screens/`)
- **MainScreen.kt** — Tab navigation container.
- **HomeScreen.kt** — Collection list with **Search Bar**, **Category Filter chips**, and supports Shimmer/Skeleton loading via `Resource.Loading`.
- **CollectionDetailScreen.kt** — Unified Scrapbook/Grid view with **Theme Customization** and **Sharing**.
- **CameraScreen.kt** — Real-time stamp capture with **GPS tagging** and realistic perforated frame.
- **CalendarScreen.kt** — Paginated month-by-month view with "Previous" and "Next" navigation. Includes **Monthly Statistics**.
- **DayStampsScreen.kt** — Detail grid for a specific date's captures.
- **StampDetailScreen.kt** — Specialized View/Edit screen for individual stamps.

#### Features
- **App Lock (Biometric)**: Enforced via `MainActivity.kt` on app start.
- **Unified Stamp Management**: Dedicated Detail/Edit screen for every stamp in the app.
- **Paginated Calendar History**: Browse your collection month-by-month with intuitive navigation.
- **Monthly Insights**: Track your monthly progress with "Total Stamps" and "Active Days" stats.
- **Advanced Camera Tools**: Support for front-facing camera and a customizable **Stamp Cutter** (Classic, Scalloped, Modern).
- **Day-Level Details**: Tap any calendar day to see a dedicated grid of stamps for that day.
- **Smart Cataloging**: Search stamps and filter by category (Flora, Fauna, etc.).
- **Premium Sharing**: Export current collection view (Scrapbook/Grid) as a high-quality image.
- **GPS Tracking**: Automatic coordinate logging for every capture.
- **Theme Engine**: Toggle between Paper, Cork, Velvet, and more for each collection.

### Utils (`com.stampcollect.util`)
- **StampImageHelper** — Processes ImageProxy: crop, adds realistic **warm parchment border**, punched circular **perforation holes** (midline centered), and subtle **inner shadow** for 3D depth.

## 3. Important Development Rules
- **State UI/UX**: Always utilize `Resource` branches (`isLoading`, `isSuccess`, `isError`) to provide optimal user feedback. Do not render empty UI blindly.
- **Image Storage**: `context.filesDir` (internal). Only `CAMERA` permission needed.
- **Default Collection**: "All" auto-created if DB is empty on boot.
- **Scrapbook Drag**: `detectDragGestures` → save X/Y offsets via `updateStampPosition()` on `onDragEnd` using background contexts (`Dispatchers.IO`).
- **Stamp Display in Grid/Scrapbook**: Show bare image ONLY, no name/description label overlay. User taps stamp to view/edit details in a dialog.
- **Tab Bar Style**: Icon-only, no labels, floating pill shape, centered at bottom.
- **Color Scheme**: High-contrast bright light theme. Deep blue for primary actions, warm parchment for collection backgrounds, dark gray text.
- **Stamp Frame**: Realistic die-cut look with circular perforations punched along the midline of a warm-white border, including an inner shadow on the photo edge.
- **DB Migrations**: Using `fallbackToDestructiveMigration()` — bump version number when schema changes.

*NOTE: Always read this file to re-familiarize yourself with the app before proposing implementations!*
