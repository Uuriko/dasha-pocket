#!/usr/bin/env node
/**
 * Commons wiring for Pocket. Run from this repo: node test/spike.test.mjs
 * Not part of dasha-desk npm test.
 */
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { createOpenBounty, fundBounty } from '../commons/loop.mjs';
import { createSimulatedTx, fakeSignature } from '../commons/tx.mjs';
import { tapeFromBounties } from '../commons/tape.mjs';

const here = dirname(fileURLToPath(import.meta.url));
const live = JSON.parse(readFileSync(join(here, '../fixtures/live-bounties.json'), 'utf8'));

assert.equal(live.schema, 'dasha-bounties-feed/v1');
assert.deepEqual(live.listings, []);

const CREATOR = 'DwpCrg5qfCMW11a9FYFsAR9ZYQUYKNhfLdnzpci7sYgb';
const tx = createSimulatedTx({ signature: fakeSignature('pocket-fund') });
let bounty = createOpenBounty({
  id: 'pocket-1',
  title: '25 USDC bounty',
  amount: '25',
  creator: { kind: 'wallet', id: CREATOR, wallet: CREATOR, handle: 'ada' },
  creatorWallet: CREATOR,
  source: { kind: 'app', community: 'getdasha', ref: null },
});
const funded = await fundBounty(bounty, tx);
assert.equal(funded.ok, true, funded.detail || funded.error);
const lines = tapeFromBounties([funded.bounty]).map((row) => row.kind);
assert.deepEqual(lines, ['created', 'funded']);
assert.match(tapeFromBounties([funded.bounty])[1].line, /funded bounty #1/);
assert.ok(funded.bounty.funding.tx.signature);
assert.doesNotMatch(JSON.stringify(funded.bounty.schema), /dasha/);

console.log('dasha-pocket spike: PASS');
