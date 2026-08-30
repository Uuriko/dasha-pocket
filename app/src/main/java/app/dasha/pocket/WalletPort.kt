package app.dasha.pocket

/**
 * Signer port. Mirrors commons/tx.mjs.
 * No keys. No auto-sign. No custody. Sign only after a user tap.
 */
data class SignIntent(
    val purpose: String,
    val bountyId: String,
    val amount: String? = null,
)

interface WalletPort {
    val name: String
    val custody: Boolean get() = false
    val autoSign: Boolean get() = false
    val seedVaultPresent: Boolean
    fun connect(): String?
    fun requestSignature(intent: SignIntent): String
}

class AutoSignForbidden : IllegalStateException("auto_sign_forbidden")
class CustodyForbidden : IllegalStateException("custody_forbidden")
class UserRejected : IllegalStateException("user_rejected")

fun WalletPort.signedAfterTap(intent: SignIntent): String {
    if (autoSign) throw AutoSignForbidden()
    if (custody) throw CustodyForbidden()
    return requestSignature(intent)
}
