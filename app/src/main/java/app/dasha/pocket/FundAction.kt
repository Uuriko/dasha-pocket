package app.dasha.pocket

/**
 * One bounty action after a tap: fund.
 * Wallet is requested here, not on browse / Tape / connect-only screens.
 */
data class FundResult(
    val signature: String,
    val bountyId: String,
)

class FundAction(private val wallet: WalletPort) {
    fun fund(bountyId: String, amount: String = "25"): FundResult {
        val signature = wallet.signedAfterTap(
            SignIntent(purpose = "funding", bountyId = bountyId, amount = amount),
        )
        return FundResult(signature = signature, bountyId = bountyId)
    }
}
