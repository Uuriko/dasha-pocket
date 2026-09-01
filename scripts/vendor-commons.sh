#!/bin/bash
# Copy the reviewed Commons leaf files from one immutable dasha-desk commit.
# No adapter.mjs. Updating the snapshot requires reviewing this file and the
# expected Git blob IDs together; an environment variable cannot silently
# switch Pocket to a different Commons branch.
set -euo pipefail

REF="d604802010f38539f5a4063b0469c6fe6591f969"
BASE="https://raw.githubusercontent.com/Uuriko/dasha-desk/${REF}/commons"
FILES="schema.mjs machine.mjs loop.mjs tape.mjs tx.mjs"

expected_blob() {
  case "$1" in
    schema.mjs) echo "037a19eda7c68e0d9ed2852c84c0851cc81f8856" ;;
    machine.mjs) echo "e6d2fd12bf29761d3dcd069b695177842652f4f6" ;;
    loop.mjs) echo "d5a50ff7c1f7494bbd177d91664c80227f984d60" ;;
    tape.mjs) echo "9a82a8eec17c053c9a53705ce4be2a9ee1412358" ;;
    tx.mjs) echo "52cffb5a868c07f2cc9570d7728094172a2a0543" ;;
    *) return 1 ;;
  esac
}

command -v curl >/dev/null || { echo "curl is required" >&2; exit 69; }
command -v git >/dev/null || { echo "git is required to verify vendored blobs" >&2; exit 69; }

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
mkdir -p commons

for f in $FILES; do
  curl --fail --silent --show-error --location \
    --proto '=https' --tlsv1.2 --retry 3 \
    "$BASE/$f" -o "$tmp/$f"

  actual="$(git hash-object "$tmp/$f")"
  expected="$(expected_blob "$f")"
  if [ "$actual" != "$expected" ]; then
    echo "commons/$f failed integrity check: expected $expected, got $actual" >&2
    exit 65
  fi

  install -m 0644 "$tmp/$f" "commons/$f"
  echo "vendored commons/$f from $REF ($actual)"
done
