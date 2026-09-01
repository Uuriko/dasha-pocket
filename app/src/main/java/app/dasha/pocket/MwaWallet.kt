package app.dasha.pocket

import android.content.Context
import android.content.pm.PackageManager

/**
 * Mobile Wallet Adapter + Seed Vault when the OS has it.
 * Connect and sign only after a tap. This file does not hold keys.
 *
 * Needs an Android device / emulator with a wallet. Not used in JVM tests.
 */
class MwaWallet(private val context: Context) : WalletPort {
    override val name = "mwa"
    override val seedVaultPresent: Boolean
        get() = context.packageManager.hasSystemFeature(SEED_VAULT_FEATURE) ||
            isPackageInstalled("com.solanamobile.seedvault")

    override fun connect(): String? {
        return null
    }

    override fun requestSignature(intent: SignIntent): String {
        throw UnsupportedOperationException(
            "MWA sign runs on device after a Fund tap. JVM uses SimulatedWallet.",
        )
    }

    private fun isPackageInstalled(pkg: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(pkg, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {
        const val SEED_VAULT_FEATURE = "com.solanamobile.seedvault"
    }
}
