# Dasha Pocket

Native Solana Mobile / Seeker client for the Commons bounty loop. **Not a WebView of getdasha.com.**

This repo is the spike. dasha-desk stays static pastes + `commons/`. Android Gradle does not run on dasha-desk CI.

## What this spike is

1. **Connect** — Mobile Wallet Adapter. Seed Vault when the OS has it.
2. **Tape** — Commons human kinds: created / funded / submitted / selected / paid / cancelled. Not `/digest`.
3. **Discovery** — public `GET https://www.getdasha.com/bounties.json` (`dasha-bounties-feed/v1`). Empty `listings` is honest.
4. **One action** — Fund, after a tap. Wallet only then. Simulated signer for tests. No custody. No auto-sign. No keys in infra.

Vendored Commons leaf files (no adapter unless you speak the getdasha feed in JS):

`commons/schema.mjs` `machine.mjs` `loop.mjs` `tape.mjs` `tx.mjs`

Kotlin parses the public JSON itself. `adapter.mjs` was not copied.

## What this is not

No APK. No Play listing. No Seeker hardware in CI. No push. No camera / IRL drops. No SKR perks. No Helius SDK. No `plugin.jup.ag`.

## Tests

```bash
node test/spike.test.mjs
```

Kotlin JUnit lives under `app/src/test/`. Needs Android SDK + Gradle. This Linux image has OpenJDK 21 and **no** Android SDK, **no** Gradle, **no** kotlinc — `assembleDebug` was not run.

## Copy from dasha-desk

See [docs/commons/CONSUME.md](https://github.com/Uuriko/dasha-desk/blob/main/docs/commons/CONSUME.md) and [docs/commons/POCKET.md](https://github.com/Uuriko/dasha-desk/blob/main/docs/commons/POCKET.md) on dasha-desk (until those land on `main`, they are on the Pocket spike PR).

Issue: [Uuriko/dasha-desk#45](https://github.com/Uuriko/dasha-desk/issues/45).
