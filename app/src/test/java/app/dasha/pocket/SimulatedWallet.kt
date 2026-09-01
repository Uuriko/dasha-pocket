package app.dasha.pocket

/**
 * Test-only signer. Mirrors the production WalletPort contract without making
 * simulated signatures reachable from the Android application source set.
 */
class SimulatedWallet(
    private val signature: String = "Simulated1111111111111111111111111111111111111111111111111111",
    private val reject: Boolean = false,
    override val seedVaultPresent: Boolean = false,
) : WalletPort {
    override val name = "simulated"
    var connectCalls = 0
    var signCalls = 0

    override fun connect(): String {
        connectCalls += 1
        return "simulated"
    }

    override fun requestSignature(intent: SignIntent): String {
        signCalls += 1
        if (reject) throw UserRejected()
        require(intent.purpose == "funding" || intent.purpose == "settlement")
        return signature
    }
}
