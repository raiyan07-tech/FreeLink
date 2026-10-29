# FREELINK — Threat Model & Security Posture

> **Threat Modeling Methodology**: STRIDE evaluation for an offline-first proximity communications companion.

---

## 1. Trust Boundaries

```
[ Uncontrolled Radio Space: BLE 2.4 GHz ]
                     │
         (Encrypted Wire Envelopes)
                     │
    ┌────────────────▼────────────────┐
    │  Android Bluetooth HAL / Radio  │
    └────────────────┬────────────────┘
                     │ (Parsed JSON, Max 4096 bytes)
    ┌────────────────▼────────────────┐
    │     FreeLink ProtocolEngine     │
    │  (Deduplication + Rate Limiter) │
    └────────────────┬────────────────┘
                     │ (AES-256-GCM AEAD Decryption)
    ┌────────────────▼────────────────┐
    │     Application Local Domain    │
    │  (Room SQLite / Internal Mem)   │
    └─────────────────────────────────┘
```

---

## 2. STRIDE Threat Analysis & Mitigations

### 2.1 Spoofing (Identity Impersonation)
* **Threat**: A malicious nearby actor configures their Bluetooth broadcast to display another user's 10-digit FreeLink ID.
* **Impact**: Target may accept connection under false pretenses.
* **Mitigation**:
  1. The 10-digit FL ID is explicitly treated as a public routing address, **not a proof of identity**.
  2. Every FreeLink identity generates a 2048-bit keypair and a SHA-256 public key fingerprint.
  3. The Contacts screen renders trust badges (`UNVERIFIED`, `VERIFIED_FINGERPRINT`, `VERIFIED_QR`).
  4. Users must compare the 8-block safety number or scan the peer's in-person QR code to verify identity.

### 2.2 Tampering (Message Alteration in Transit)
* **Threat**: An adversary on the local 2.4 GHz radio path intercepts and modifies transit packets.
* **Impact**: Manipulation of emergency coordinates or travel instructions.
* **Mitigation**:
  1. All message bodies are protected with **AES-256-GCM** authenticated encryption (`AES/GCM/NoPadding`).
  2. Any bit-flip in the ciphertext or tampering with the 96-bit initialization vector causes `AEADBadTagException` during decryption, immediately discarding the packet.

### 2.3 Repudiation (Denial of Message Origination)
* **Threat**: A sender claims they never sent an emergency SOS or local text.
* **Impact**: Disputed communication logs.
* **Mitigation**:
  1. Every message envelope carries a unique UUID, protocol version, sender FL ID, recipient FL ID, and timestamp.
  2. Acknowledgement envelopes explicitly link back to the originating message ID (`createAckEnvelope`).

### 2.4 Information Disclosure (Eavesdropping & Leakage)
* **Threat**: Passive BLE sniffing in public spaces (railway platforms, airports, bus stations).
* **Impact**: Leakage of phone numbers, names, real-time GPS locations, or private keys.
* **Mitigation**:
  1. **Minimal BLE Advertisements**: Broadcast packets only transmit the service UUID `0000FEF0-0000-1000-8000-00805F9B34FB` and the prefixed FL ID (`FL-XXXXXXXXXX`). No real names, phone numbers, or coordinates are broadcast.
  2. **QR Code Integrity**: The QR generator encodes only `flId`, `displayName`, and `publicKeyFingerprint`. Private keys and device credentials **never** leave Android secure storage.
  3. **Zero Sensitive Logging**: Diagnostic logs capture only integer error codes and protocol events; message bodies and cryptographic keys are strictly excluded from Android Logcat.

### 2.5 Denial of Service (Radio Flooding & Resource Exhaustion)
* **Threat**: A rogue device floods high volumes of large packets to crash nearby FreeLink apps or exhaust phone battery.
* **Impact**: Memory exhaustion (OOM), battery drain, unresponsiveness during critical travel moments.
* **Mitigation**:
  1. **Strict Wire Cap**: Payloads exceeding `MAX_WIRE_SIZE_BYTES` (4096 bytes) are dropped prior to parsing.
  2. **Rate Limiting**: `ProtocolEngine` restricts any individual sender FL ID to a maximum of 15 envelopes per minute.
  3. **Sliding Deduplication Cache**: Re-transmitted or re-broadcast packets with known UUIDs are acknowledged and discarded without re-allocating database entries.
  4. **Timed Scanning**: Bluetooth discovery does not run perpetually; users explicitly start and stop scanning.

### 2.6 Elevation of Privilege & False Capabilities
* **Threat**: Misleading users into believing the app can bypass operator billing to make free cellular voice calls.
* **Impact**: Stranded travelers fail to seek legitimate emergency assistance.
* **Mitigation**:
  1. FreeLink never creates fake signal bars or mock carrier interfaces.
  2. Emergency 112 calls are handed directly to the Android OS via `Intent.ACTION_DIAL`. FreeLink never intercepts, records, or proxies emergency telephone traffic.

### 2.7 Financial & Wi-Fi Phishing Protection
* **Threat**: Stranded travelers connecting to untrusted open Wi-Fi or fake captive portals to recharge their expired balance.
* **Impact**: Compromise of UPI PINs, ATM card details, or banking passwords.
* **Mitigation**:
  1. Reconnect Helper includes a prominent **Zero-Trust Warning** advising travelers never to enter UPI PINs or passwords on portal pages.
  2. FreeLink does not collect or store payment credentials.
  3. Links route only to verified, official telecom portals (Airtel, Jio, Vi, BSNL).
