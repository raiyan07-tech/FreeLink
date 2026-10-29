# FREELINK — Implemented Features & Honest Product Roadmap

> **Quality Principle**: We distinguish working features from future milestones. We never disguise a mock or unimplemented protocol as production-ready.

---

## 1. Features Fully Implemented & Testable Today

| Feature | Implementation Details | Status |
|---|---|---|
| **Emergency 112 Action** | Native `Intent.ACTION_DIAL` launching `tel:112` safely via the Android system dialler. Never places unauthorized calls. | **Production-Ready** |
| **Offline Safety Checklist** | Structured emergency guidelines (landmarks, CISF/station staff, extreme battery saver) available with zero network. | **Production-Ready** |
| **Offline Travel Kit** | Complete local profile (primary carrier, USSD codes, trusted contacts, destination, blood group, medical notes). | **Production-Ready** |
| **Room Local Persistence** | SQLite database with reactive `Flow` queries for profiles, contacts, messages, and travel kits. | **Production-Ready** |
| **10-Digit FL ID System** | Deterministic allocation of provisional IDs and cryptographic 2048-bit keypair generation. | **Production-Ready** |
| **Public QR Contact Exchange** | ZXing QR generation encoding strictly public identity metadata; zero private keys or passwords exposed. | **Production-Ready** |
| **Real Android BLE Transport** | `BluetoothLeTransport` utilizing `BluetoothLeScanner`, `BluetoothLeAdvertiser`, and `BluetoothGattServer` for real hardware. | **Production-Ready** |
| **Developer Demo Simulation** | `SimulationTransport` allowing full offline discovery, consent modals, and message ACK testing on emulators. | **Production-Ready** |
| **Wire Protocol & Envelope** | Bounded 4096-byte wire envelopes, message UUIDs, timestamps, and expiry parameters. | **Production-Ready** |
| **AES-256-GCM Encryption** | Authenticated Encryption with Associated Data (AEAD) using standard Android `javax.crypto` algorithms. | **Production-Ready** |
| **Anti-Replay & Rate Limiting** | Sliding window deduplication cache (500 items) and per-peer rate limiting (15 envelopes/min). | **Production-Ready** |
| **Delivery State Machine** | Honest progression: `QUEUED_LOCALLY` -> `SENDING` -> `SENT_TO_PEER` -> `ACKNOWLEDGED_BY_PEER` / `FAILED`. | **Production-Ready** |
| **Telecom Reconnect Helper** | USSD codes and official portal links for Airtel, Reliance Jio, Vodafone Idea (Vi), and BSNL. | **Production-Ready** |
| **Dark Minimalist Design** | Deep navy/black surfaces (`#0B0E14`), electric blue primary actions, cyan highlights, high-contrast typography. | **Production-Ready** |

---

## 2. Unimplemented Items & Future Roadmap

### Milestone 6: Offline Voice Calls (Phase Two)
* **Status**: **Not Implemented / Architecture Ready**
* **Technical Reason**: Stable real-time audio over local ad-hoc connections requires high bandwidth links (such as Wi-Fi Direct or Bluetooth L2CAP socket channels) coupled with an Opus audio pipeline (20ms framing, jitter buffers, acoustic echo cancellation, and foreground microphone services).
* **Next Engineering Action**: Implement local peer-to-peer signaling over BLE to negotiate Wi-Fi Direct sockets, then integrate an embedded Opus codec library.

### Milestone 7: Multi-Hop Store-and-Forward Mesh Relaying
* **Status**: **Research Phase**
* **Technical Reason**: Multi-hop relaying without internet infrastructure introduces complex attack surfaces, including routing loops, battery-drain attacks, and unsolicited relay spam.
* **Next Engineering Action**: Develop an opportunistic epidemic routing protocol with mandatory user consent for relaying, strict storage budgets (max 5 MB relay cache), and hop-by-hop cryptographic signatures.

### Automated Carrier Direct Balance Top-Up
* **Status**: **Deliberately Excluded**
* **Technical Reason**: Recharging a SIM card directly from within an app requires verified carrier billing APIs, PCI-DSS compliance, or active data links for bank UPI SDKs. Claiming to recharge a prepaid balance while offline would be fraudulent.
* **Current Honest Solution**: The app provides verified carrier USSD codes (which function over cellular control channels without data) and links to official portals once a Wi-Fi connection is secured.
