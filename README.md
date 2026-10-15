# FooTube — Android App

The native Android client of **FooTube**, a YouTube-style video platform built by a team of three as a Bar-Ilan University Advanced Programming project.

## Features

- **Video feed** with pull-to-refresh and search
- **Video player** with likes, sharing and a comment section (add, edit, delete)
- **Add and edit videos** from the device
- **Five-step sign-up flow** and log-in screen
- **Side navigation drawer** with a dark-mode toggle
- Sample videos bundled in `assets/data.json`, parsed with Gson

## The FooTube project

| Repository | What it is |
|------------|------------|
| [FooTube-Server](https://github.com/elayben/FooTube-Server) | Node.js + Express REST API with MongoDB |
| [FooTube-Web](https://github.com/elayben/FooTube-Web) | React web client |
| **FooTube-Android** (this repo) | Native Android client (Java) |
| [FooTube-CPP-Server](https://github.com/elayben/FooTube-CPP-Server) | Multithreaded C++ TCP server for video recommendations |

## Tech stack

Java · Android SDK (min 24, target 34) · Gradle (Kotlin DSL) · Material Components · RecyclerView · SwipeRefreshLayout · Gson

## Getting started

**Prerequisites:** Android Studio (Hedgehog or newer) with an emulator or a connected device.

1. Clone the repository:
   ```bash
   git clone https://github.com/elayben/FooTube-Android.git
   ```
2. Open the folder in Android Studio and let Gradle sync.
3. Run the `app` configuration on an emulator or device.

The app opens on the video feed. Log in from the top bar, or register a new user first.

## Project structure

```
app/src/main/java/com/example/myyoutube/
├── MainActivity.java          # Feed, search, navigation drawer
├── VideoPlayerActivity.java   # Player, likes, share, comments
├── AddEditVideoActivity.java  # Upload / edit a video
├── logInScreen1.java          # Log in
├── signInScreen1-5.java       # Multi-step registration
├── adapters/                  # RecyclerView adapters
└── classes/                   # Video, User, Comment models + managers
```

## Team

Built by a team of three: one developer owned the video screens on web, one owned the same screens on Android, and one owned login and registration on both platforms. Work was planned and tracked in Jira. [Elay Ben Yehoshua](https://github.com/elayben) was one of the three developers.

The original team repository is [ayalbir/Android](https://github.com/ayalbir/Android).
