package app.dasha.pocket

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PocketSpikeTest {
    @Test
    fun packageAndVersionAreCanonical() {
        assertEquals("app.dasha.pocket", BuildConfig.APPLICATION_ID)
        assertEquals("0.1.0", BuildConfig.VERSION_NAME)
        assertEquals(1, BuildConfig.VERSION_CODE)
    }

    @Test
    fun emptyListingsAreHonest() {
        val json = javaClass.classLoader!!
            .getResourceAsStream("live-bounties.json")!!
            .bufferedReader()
            .readText()
        val feed = FeedClient.parse(json)
        assertEquals(FEED_SCHEMA, feed.schema)
        assertTrue(feed.empty)
        assertEquals(0, feed.listings.size)
    }

    @Test
    fun tapeKeepsHumanKindsAndDedupe() {
        val tape = TapeStore()
        val line = TapeLine("funded:1:sig", TapeKind.funded, "alice funded bounty #1", "chain", "sig", true)
        assertTrue(tape.ingest(line))
        assertFalse(tape.ingest(line.copy(line = "dup")))
        assertEquals(1, tape.entries().size)
        assertEquals(TapeKind.funded, tape.entries()[0].kind)
    }

    @Test
    fun fundSignsOnlyAfterTap() {
        val wallet = SimulatedWallet()
        assertEquals(0, wallet.signCalls)
        val result = FundAction(wallet).fund("pocket-1")
        assertEquals(1, wallet.signCalls)
        assertEquals("pocket-1", result.bountyId)
        assertTrue(result.signature.isNotEmpty())
    }

    @Test
    fun rejectAndAutoSignAreFailures() {
        try {
            FundAction(SimulatedWallet(reject = true)).fund("pocket-1")
            fail("expected reject")
        } catch (_: UserRejected) {}
        val evil = object : WalletPort {
            override val name = "evil"
            override val autoSign = true
            override val seedVaultPresent = false
            override fun connect() = "x"
            override fun requestSignature(intent: SignIntent) = "nope"
        }
        try {
            FundAction(evil).fund("pocket-1")
            fail("expected auto-sign forbid")
        } catch (_: AutoSignForbidden) {}
    }
}
