# ⚡ ShadowFXvibe CotCut

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Theme](https://img.shields.io/badge/Design-Premium%20Dark%20%230F0F0F-00E5FF?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**A high-performance, dark-themed kinetic typography and video captioning studio for Android.**  
*Inspired by Captions.ai and CapCut with signature Neon Cyan FX styling.*

[📱 Download Latest APK](https://github.com/HossainShuvo0o/ShadowFXvibe-CotCut/actions) • [✨ Features](#-key-features) • [🚀 How to Build](#-how-to-build)

</div>

---

## 📖 Overview

**ShadowFXvibe CotCut** is an advanced mobile video captioning and typography manipulation studio. Designed specifically for content creators, reels editors, and mobile cinematographers, it combines an ultra-modern `#0F0F0F` dark interface with radiant `#00E5FF` neon accents.

Featuring fluid drag-and-drop kinetic text positioning, dynamic font rendering, audio waveform inspection, and multi-track timelines, ShadowFXvibe CotCut empowers creators to craft viral typography videos with zero latency.

---

## ✨ Key Features

- **🎬 Captions.ai-Inspired Studio Dashboard**:
  - Direct "+ Import a Video" creation card.
  - Category filters: *Trending, New, Bold, Polished, Minimal*.
  - Instant project playback and real-time caption previews.

- **🔤 Kinetic Captioning & Gestures**:
  - Ultra-smooth `detectDragGestures` with consumed pointer tracking (zero-jitter).
  - Multi-track timeline (Video, Audio waveform visualization, and dynamic Text tracks).
  - Real-time word-by-word caption synchronization.

- **🎨 Deep Customization Control Panel**:
  - **Color**: Preset vibrant palettes (Neon Cyan, Electric Purple, Sunset Gold, Fire Red, Emerald Green) and custom alpha sliders.
  - **Font**: Curated Google typography suite (*Poppins, Montserrat, Hind Siliguri, Anek Bangla, Creepster, Nosifer, Shrikhand*).
  - **Breaks**: Auto sentence tokenizer, word count chunking, and smart line-break insertions.
  - **Transitions**: 7 dynamic motion presets (*Zoom In, Zoom Out, Pop, Scale, Slide Down, Slide Up, Fade In*) plus movement & rotation randomization toggles.

- **🎭 Masking & Blending Modes**:
  - Linear, Circle, and Rectangular geometric masking shaders.
  - Screen, Overlay, and Multiply composite blending modes.

- **💾 Pro Export Engine**:
  - Full-resolution rendering (1080p FHD & 4K Ultra HD).
  - Variable frame rates: 24 FPS (Cinematic), 30 FPS (Standard), 60 FPS (Silky Smooth).
  - One-tap direct export to Android MediaStore Gallery.

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material 3 Dynamic System)
- **Video Engine:** AndroidX Media3 / ExoPlayer
- **Graphics & Shaders:** Compose Graphics & Native Canvas Blending
- **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (StateFlow)
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`) with Version Catalog
- **CI/CD:** Automated GitHub Actions APK Compiler

---

## 📱 Screenshots & Design System

| Studio Dashboard | Kinetic Timeline & Canvas | Pro Control Panel |
|:---:|:---:|:---:|
| *Captions.ai Inspired Layout* | *Interactive Gestures & Masking* | *Typography, Breaks & Motion FX* |
| `#0F0F0F` Deep OLED | `#00E5FF` Neon Accent | `#1A1A1A` Elevated Surface |

---

## 🚀 How to Build

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17
- Android SDK 34+

### Local Build Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/HossainShuvo0o/ShadowFXvibe-CotCut.git
   cd ShadowFXvibe-CotCut
   ```
2. Build the Debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. Locate the compiled APK:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🤖 CI/CD: Automated APK Builds

Every push or pull request to the `main` branch triggers the **Build ShadowFXvibe Cot Cut APK** GitHub Actions workflow:
1. Clones source code and configures JDK 17.
2. Compiles production-ready debug APK with Gradle.
3. Packages and uploads `ShadowFXvibe-CotCut-Debug-APK` directly to the repository's **Actions Artifacts**.

---

## 👤 Author

Developed with ❤️ by **[Hossain Shuvo](https://github.com/HossainShuvo0o)**

*Created for high-octane kinetic video creators.*
