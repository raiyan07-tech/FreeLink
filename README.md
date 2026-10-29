# FREELINK

> **"Stay connected when your recharge runs out."**  
> A practical travel-safety and connectivity companion for the moment mobile data or outgoing service stops working.

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-brightgreen.svg)]()
[![Language](https://img.shields.io/badge/Language-Kotlin%202.0.21-blue.svg)]()
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-purple.svg)]()
[![Security](https://img.shields.io/badge/Cryptography-AES--256--GCM-orange.svg)]()
[![Offline First](https://img.shields.io/badge/Storage-Room%20(SQLite)-success.svg)]()
[![Download APK](https://img.shields.io/badge/Download-FreeLink%20v1.0.0%20APK-2F6BFF?style=for-the-badge&logo=android&logoColor=white)](release/FreeLink-v1.0.0-debug.apk)

---

## 📥 Download & Install APK

You can download the pre-compiled, runnable Android application package directly from this repository:

| Artifact | Version | Target SDK | Download Link |
|---|---|---|---|
| **FreeLink Debug APK** | `1.0.0` | Android 15 (API 35) | [**⬇️ Download FreeLink-v1.0.0-debug.apk (17.5 MB)**](release/FreeLink-v1.0.0-debug.apk) |

*Local build path: [`app/build/outputs/apk/debug/app-debug.apk`](app/build/outputs/apk/debug/app-debug.apk)*

### Quick Device Installation
* **Option A: Direct ADB Install** (with phone plugged in via USB and USB Debugging enabled):
  ```powershell
  adb install release/FreeLink-v1.0.0-debug.apk
  ```
  *(or `.\gradlew.bat installDebug`)*

* **Option B: Sideload onto Handset**:
  1. Transfer `FreeLink-v1.0.0-debug.apk` to your phone via USB cable, Google Drive, or local share.
  2. Open the file on your device and tap **Install** (allow *"Install unknown apps"* if prompted).
  3. Launch **FREELINK** directly from your app drawer.

---

## 1. Product Vision & The Honest Technical Boundary

### The Problem We Are Solving
When traveling by train, bus, or in unfamiliar areas, a commuter's prepaid balance or daily data quota can expire mid-journey. With mobile data disabled, recharge apps, UPI payments, and online messaging stop working. Although cellular signal bars may appear on the status bar, normal outgoing calls are barred by the telecom operator.

### Non-Negotiable Technical Truth
* **An app cannot bypass a telecom operator's billing or call-routing restrictions.** FREELINK never claims to provide "free carrier calls or SMS to any phone number without recharge".
* **Signal bars ≠ Active service entitlement.** Cellular radio access is operator-controlled.
* **Emergency Calls (112)**: India's emergency number `112` (Emergency Response Support System — ERSS) is routed directly by the device's cellular radio and operating system, often connecting even when account balance is zero. FREELINK hands this off to the native phone dialler.
* **Nearby Offline Messaging**: Bluetooth Low Energy (BLE) enables nearby devices in physical proximity to exchange encrypted text messages without cellular reception or active internet.

---

## 2. Core Feature Set

1. **Emergency 112 Access**:
   - Prominent, high-contrast one-tap trigger launching the native phone dialler with `112` prefilled (`ACTION_DIAL`).
   - Built-in offline emergency checklist (landmarks, station security/police, extreme battery saver).
   - Structured offline SOS text message builder with clipboard export.
2. **Offline Travel Safety Kit**:
   - Stores trusted contacts, primary mobile carrier, user recharge notes, destination address, and emergency medical details (blood group, allergies).
   - 100% offline, persistent SQLite storage powered by Room.
3. **Nearby Device Discovery & Encrypted Messaging**:
   - Real Android Bluetooth LE scanning, advertising, and GATT characteristic transmission.
   - Built-in **Developer Demo Simulation Mode** toggle for reliable single-device and emulator demonstrations.
   - Explicit peer consent dialogs before establishing connections.
   - Delivery state machine: `QUEUED_LOCALLY` ➔ `SENDING` ➔ `SENT_TO_PEER` ➔ `ACKNOWLEDGED_BY_PEER` (or `FAILED` with retry).
   - AES-256-GCM authenticated encryption for transit payloads.
4. **10-Digit FREELINK ID & QR Exchange**:
   - Unique 10-digit account identifier and 2048-bit cryptographic keypair.
   - Dynamic QR code generation for exchanging contact cards in physical proximity.
   - SHA-256 public key fingerprint comparison to guard against spoofed identities.
   - Strict privacy: No passwords, private keys, or location data in QR payloads.
5. **Telecom Reconnection Helper**:
   - Verified USSD balance and menu codes for **Airtel** (`*121#`), **Jio** (`*333#`), **Vodafone Idea** (`*199#`), and **BSNL** (`*123#`).
   - Official direct recharge URLs.
   - Zero-Trust security advice warning travelers against entering UPI PINs or passwords on untrusted open Wi-Fi captive portals.
6. **Settings & Privacy**:
   - Peer visibility controls (Discoverable vs Hidden).
   - Message retention and automated 24-hour expiry purge.
   - One-tap local data wipe for complete privacy.

---

## 3. Technology Stack

* **Operating System**: Android 8.0 (API 26) through Android 15 (API 35).
* **Architecture**: Clean Architecture / Offline-first Repository Pattern.
* **Language**: Kotlin 2.0.21.
* **UI Toolkit**: Jetpack Compose with Material 3 Dark Minimalist Palette (`#0B0E14` Navy Background, `#2F6BFF` Electric Blue, `#00E5FF` Cyan, `#EF4444` Emergency Red).
* **Local Persistence**: Android Jetpack Room 2.6.1 (SQLite) with Kotlin Symbol Processing (KSP).
* **Radios**: Android Bluetooth LE (`BluetoothLeScanner`, `BluetoothLeAdvertiser`, `BluetoothGattServer`).
* **Security & Crypto**: Standard Java/Android `javax.crypto` (AES-256-GCM AEAD, SHA-256 Fingerprints).
* **QR Matrix**: ZXing Core 3.5.3 (pure Java encoding, lightweight).
* **Build System**: Gradle 8.13 with Android Gradle Plugin 8.7.3.

---

## 4. Project Structure

```
FreeLink/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/freelink/app/
│   │   │   ├── FreeLinkApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── domain/               # Domain models, enums & repository interfaces
│   │   │   ├── data/local/           # Room Database, entities, DAOs & repositories
│   │   │   ├── identity/             # 10-digit FL ID, keypair generation, QR codec
│   │   │   ├── nearby/               # BLE transport, simulation transport & connection manager
│   │   │   ├── messaging/            # Protocol envelope, AES-256-GCM crypto & deduplication
│   │   │   ├── safety/               # Emergency 112 dialler intent & offline checklist
│   │   │   ├── online/               # Network monitor & telecom operator directory
│   │   │   └── ui/                   # Jetpack Compose theme, navigation & 10 screens
│   │   └── res/                      # Drawables, colors, strings, adaptive launcher icons
│   └── src/test/                     # Comprehensive JVM unit tests (Protocol, Crypto, Identity)
├── docs/
│   ├── ARCHITECTURE.md               # Detailed architecture diagrams & protocol specs
│   ├── THREAT_MODEL.md               # STRIDE threat assessment & security posture
│   ├── TEST_MATRIX.md                # Multi-device physical testing instructions
│   └── UNIMPLEMENTED_ROADMAP.md       # Honest roadmap distinguishing Phase 1 from Phase 2
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew.bat
```

---

## 5. Build and Execution Instructions

### Prerequisites
* **Java Development Kit (JDK)**: JDK 17 or higher.
* **Android SDK**: Compile SDK 35 (installed in Android Studio or standard SDK location).

### Build Commands
1. **Compile & Assemble Debug APK**:
   ```bash
   .\gradlew.bat assembleDebug
   ```
   *The generated APK is located at: `app/build/outputs/apk/debug/app-debug.apk`*

2. **Run All Unit Tests**:
   ```bash
   .\gradlew.bat test
   ```
   *Runs comprehensive test suites covering AES-256-GCM encryption, wire envelope serialization, duplicate suppression, rate limiting, and FL ID validators.*

3. **Install on Connected Android Device or Emulator**:
   ```bash
   .\gradlew.bat installDebug
   ```

---

## 6. How to Run the Hackathon / Reviewer Demo

1. **Demo 1: Cold Offline Launch (Airplane Mode)**
   - Turn on Airplane Mode on your phone; ensure Wi-Fi and Cellular are off.
   - Open **FREELINK**.
   - Notice the app opens instantly with a calm **"Offline Mode Active"** card.
   - Navigate to **Offline Travel Kit** to see your locally stored contacts and notes without internet.

2. **Demo 2: Safe Emergency 112 Dialler Routing**
   - Tap the prominent **"EMERGENCY CALL — 112"** button on the Home screen.
   - Observe that the Android native dialler opens with `112` prefilled.
   - *(Notice: FREELINK never places unauthorized test calls).*

3. **Demo 3: Developer Demo Mode Simulation (Single Device / Emulator)**
   - Navigate to **Nearby Devices**.
   - Toggle **"Developer Demo Mode"** to ON.
   - Tap **"Scan for Peers"** ➔ See simulated nodes (`Rahul`, `Station Volunteer`) discovered.
   - Tap **"Connect"** ➔ Tap **"Chat"**.
   - Type a short text and tap Send.
   - Observe real-time state progression: `QUEUED_LOCALLY` ➔ `SENDING` ➔ `SENT_TO_PEER` ➔ `ACKNOWLEDGED_BY_PEER`, followed by an encrypted peer reply.

4. **Demo 4: Real Two-Phone Hardware BLE Exchange**
   - On Phone A: Navigate to Nearby, toggle Demo Mode OFF, and tap **"Broadcast"**.
   - On Phone B: Navigate to Nearby, toggle Demo Mode OFF, and tap **"Scan for Peers"**.
   - Phone B taps **"Connect"** on Phone A's card.
   - Phone A receives the **Peer Consent Dialog** and accepts.
   - Both devices exchange encrypted messages locally over Bluetooth LE.

5. **Demo 5: Public FL ID & QR Contact Card**
   - Navigate to **My FreeLink ID**.
   - Observe the 10-digit FL ID and dynamically rendered QR code.
   - Inspect the QR card payload to verify zero private keys or passwords are leaked.

---

## 7. Documentation Index
* [System Architecture Specification](docs/ARCHITECTURE.md)
* [Threat Model & Security Evaluation](docs/THREAT_MODEL.md)
* [Test Matrix & Protocols](docs/TEST_MATRIX.md)
* [Implemented vs Roadmap Comparison](docs/UNIMPLEMENTED_ROADMAP.md)

---

## 8. License & Attribution
Designed and built for travel safety, low-connectivity resilience, and emergency recovery. Licensed under the MIT License.
