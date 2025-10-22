# ViralNexus

A pandemic simulation mobile game for Android, built with Kotlin and Jetpack Compose.

## Overview

ViralNexus is a strategic simulation game where players control a pathogen and must evolve it to infect the global population while avoiding detection and countermeasures. The game features:

- **Modern Android Stack**: Kotlin, Jetpack Compose, Material3
- **3D Graphics**: OpenGL ES 3.0 for immersive world visualization
- **Realistic Simulation**: 56 countries with accurate population and geographic data
- **Strategic Gameplay**: Complex upgrade systems for transmission, symptoms, and abilities

## Development on Android Phone (Termux)

This project is developed entirely on an Android phone using Termux. Since AAPT2 (required for building APKs) doesn't run natively on Android, we use **GitHub Actions** for cloud builds.

### Workflow

1. **Edit code on phone** (Termux with Android Studio code editor or vim/nano)
2. **Commit changes** with git
3. **Push to GitHub**
4. **GitHub Actions automatically builds the APK**
5. **Download the built APK** from GitHub Actions artifacts

### Setup on Phone

```bash
# Install required packages
pkg install git openjdk-21 gradle

# Set up Android SDK environment
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
export PATH=$ANDROID_HOME/platform-tools:$PATH

# Clone the repository
git clone https://github.com/YOUR_USERNAME/ViralNexus.git
cd ViralNexus

# Edit code
nano app/src/main/java/com/viralnexus/game/MainActivity.kt

# Commit and push
git add .
git commit -m "Update game mechanics"
git push origin master
```

### Getting Your APK

After pushing, go to your GitHub repository:
1. Click on **Actions** tab
2. Click on the latest workflow run
3. Download the **debug-apk** or **release-apk** artifact
4. Transfer to your phone and install

## Technical Specifications

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Kotlin**: 1.9.20
- **Gradle**: 8.13
- **Android Gradle Plugin**: 8.1.2

## Project Structure

```
ViralNexus/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/viralnexus/game/
│   │   │   │   ├── MainActivity.kt          # App entry point
│   │   │   │   ├── GameEngine.kt            # Core game logic
│   │   │   │   ├── WorldMap.kt              # 3D world rendering
│   │   │   │   ├── ui/                      # Compose UI components
│   │   │   │   └── data/                    # Data models
│   │   │   ├── res/                         # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                            # Unit tests
│   └── build.gradle                         # App build config
├── .github/
│   └── workflows/
│       └── build-apk.yml                    # CI/CD pipeline
├── build.gradle                             # Project build config
├── settings.gradle
└── gradle.properties

## Current Status

**Phase**: Early prototype
**Playable**: Partially
**Features Implemented**:
- 3D world rendering with OpenGL ES
- Basic game UI with Compose
- Country infection mechanics
- Touch controls (rotation, zoom)

**TODO**:
- Save/load system
- Upgrade tree UI
- Tutorial system
- Settings screen
- Enhanced game mechanics

See [ViralNexus-Project.md](ViralNexus-Project.md) for detailed development plan.

## Building Locally (Requires x86-64 System)

If you have access to a regular computer:

```bash
./gradlew assembleDebug
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Contributing

This is a personal learning project, but suggestions and bug reports are welcome!

## License

TBD
