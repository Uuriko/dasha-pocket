# Dasha Pocket

Native Solana Mobile / Seeker client for the Commons bounty loop. **Not a WebView of getdasha.com.**

This repository is the Android spike. `dasha-desk` retains the token-agnostic Commons schemas; Pocket consumes a reviewed snapshot without adding Android tooling to the web repository.

## Current proof

- Android/Kotlin app source with wallet-free bounty-feed browsing and Seed Vault availability detection.
- A `WalletPort` boundary that requests signing only after an explicit Fund action.
- Simulated signing exists only in test source; the production app cannot display a simulated signature.
- The public `dasha-bounties-feed/v1` response is parsed without requiring a wallet. Empty `listings` is valid.
- GitHub Actions runs JVM tests, Android lint, `assembleDebug`, and uploads the debug APK plus lint report.
- Gradle 8.9 wrapper is committed. Its JAR and distribution checksums match Gradle's published release values.
- Vendored Commons files are pinned to immutable dasha-desk commit `d604802010f38539f5a4063b0469c6fe6591f969` and checked against expected Git blob IDs before replacement.

## Reproduce

Requirements: JDK 17 and Android SDK 35.

```bash
bash scripts/vendor-commons.sh
node test/spike.test.mjs
./gradlew --no-daemon testDebugUnitTest lintDebug assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Product boundary

1. **Connect** — Mobile Wallet Adapter is the intended authorization route. Seed Vault is detected where the OS exposes it.
2. **Tape** — Commons events use human-readable states such as created, funded, submitted, selected, paid, and cancelled. This is not the price digest.
3. **Discovery** — public `GET https://www.getdasha.com/bounties.json`; no wallet or login required.
4. **Fund** — the wallet boundary is reached only after a user tap. No custody, automatic signing, embedded key, or server signer.

Vendored Commons leaf files, excluding the getdasha-specific adapter:

`commons/schema.mjs` `machine.mjs` `loop.mjs` `tape.mjs` `tx.mjs`

## Remaining gates

This is not yet a production wallet or dApp Store release. `MwaWallet.connect()` and transaction signing remain deliberately unimplemented. Before grant or store claims, the project still needs:

- upgrade and compile against the maintained MWA client API;
- real authorize, cancel, reauthorize, and disconnect behavior;
- exact USDC transaction construction and wallet preview;
- a harmless submitted and finalized devnet transaction;
- ambiguous timeout reconciliation before retry;
- Seeker/compatible-device testing;
- release signing, Publisher Portal submission, and dApp Store review.

No mainnet payment, Play listing, push, camera/location drop, SKR feature, Helius integration, or live Seed Vault signing is claimed.

Tracking: [Dasha Pocket grant issue](https://github.com/Uuriko/dasha-desk/issues/45) and [Android reproducibility issue](https://github.com/Uuriko/dasha-pocket/issues/2).
