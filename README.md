## Journal (Android App)
<img width="1280" height="2856" alt="Screenshot_20251218_145530" src="https://github.com/user-attachments/assets/dc395728-48ab-4113-979c-bfbe10c82675" />
<img width="1280" height="2856" alt="Screenshot_20251218_145521" src="https://github.com/user-attachments/assets/86885a26-ca34-4b2b-ad2e-715553ba66e7" />
<img width="1280" height="2856" alt="Screenshot_20251218_145404" src="https://github.com/user-attachments/assets/67f8707c-b386-4243-953c-774d7260f8e3" />
<img width="1280" height="2856" alt="Screenshot_20251218_145339" src="https://github.com/user-attachments/assets/8b1997ad-f72a-4431-8c96-85c615abc88f" />
<img width="1280" height="2856" alt="Screenshot_20251218_145320" src="https://github.com/user-attachments/assets/69ba5de0-3686-4205-ba07-177946984549" />
<img width="1280" height="2856" alt="Screenshot_20251218_145242" src="https://github.com/user-attachments/assets/348b908d-08c0-43b7-a3d3-cb25c4e4f38a" />

A simple journaling app built with **Jetpack Compose**. Create multiple journals, write entries, attach an image, and track how you’re feeling with a mood slider.

### What you can do

- **Create journals**: Add/edit/delete journals (name, color, icon).
- **Write entries**: Create, edit, delete entries with **title + content**.
- **Add an image**: Pick an image from your device and attach it to an entry.
- **Share**: Share entry text via Android’s share sheet.
- **Mood tracking**: Use a 0–100 slider that maps to a `MoodState` (Very Unsatisfied → Very Satisfied) with animations and descriptors.
- **Calendar browsing**: Pick a date and see entries for that day, with quick previous/next day navigation.

### Tech stack

- **UI**: Jetpack Compose + Material 3
- **Navigation**: `androidx.navigation:navigation-compose`
- **Persistence**: Room (`Journal` + `JournalEntry` tables)
- **Async**: Kotlin Coroutines + Flow
- **Images**: Coil (`AsyncImage`)
- **Animations**: Lottie Compose (assets under `app/src/main/assets/`)

### Project structure (high level)

- **App entry**: `app/src/main/java/com/example/journal/MainActivity.kt`
- **Navigation graph**: `app/src/main/java/com/example/journal/navigation/JournalNavigation.kt`
- **Data layer**:
  - Room DB: `app/src/main/java/com/example/journal/data/database/JournalDatabase.kt`
  - Repository: `app/src/main/java/com/example/journal/data/repository/JournalRepository.kt`
  - Models: `app/src/main/java/com/example/journal/data/model/`
- **UI**:
  - Screens: `app/src/main/java/com/example/journal/ui/screens/`
  - Components: `app/src/main/java/com/example/journal/ui/components/`

### Requirements

- **Android Studio**: a recent stable version
- **JDK**: **17** (recommended for modern Gradle/AGP)
- **Android SDK**: install the platform matching `compileSdk` (currently **36**) via **SDK Manager**

### Run the app (Android Studio)

1. Open Android Studio.
2. **Open** the folder: `journal/`
3. Let Gradle sync finish (first sync can take a while).
4. Select a device (emulator or USB device).
5. Press **Run**.

### Troubleshooting

- **Gradle sync fails / weird build errors after moving machines**:
  - Try: **Build → Clean Project**, then **Build → Rebuild Project**
  - If needed: **File → Invalidate Caches / Restart**

- **Android SDK not found**:
  - Ensure SDK is installed via **SDK Manager**
  - If you’re building from terminal, you may need a `local.properties` file in `journal/` pointing to your SDK (Android Studio usually creates this automatically).

- **Data resets after app updates** (dev note):
  - The database is currently configured with `fallbackToDestructiveMigration()`, which can wipe local data when the schema version changes during development.
