package app.dasha.pocket

/**
 * CI / JVM signer. Same contract as commons createSimulatedTx.
 * Never used unless tests or an explicit demo tap ask for it.
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
