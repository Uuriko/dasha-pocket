#!/bin/bash
# Copy Commons leaf files from dasha-desk. No adapter.mjs.
set -euo pipefail
REF="${COMMONS_REF:-cursor/commons-consume-51b9}"
BASE="https://raw.githubusercontent.com/Uuriko/dasha-desk/${REF}/commons"
mkdir -p commons
for f in schema.mjs machine.mjs loop.mjs tape.mjs tx.mjs; do
  curl -fsSL "$BASE/$f" -o "commons/$f"
  echo "vendored commons/$f from $REF"
done
