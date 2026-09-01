package app.dasha.pocket

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

/**
 * Native screens. Not a WebView of getdasha.com.
 * Browse Tape + feed with no wallet. The wallet boundary is reached only after a tap,
 * but real MWA authorization, signing, submission, and confirmation are not implemented yet.
 */
class MainActivity : AppCompatActivity() {
    private val tape = TapeStore()
    private val io = Executors.newSingleThreadExecutor()
    private lateinit var wallet: WalletPort
    private lateinit var body: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallet = MwaWallet(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val title = TextView(this).apply { text = "Pocket"; textSize = 28f }
        val lede = TextView(this).apply { text = "USDC on Solana. We don't hold it." }
        body = TextView(this).apply { text = walletStatusCopy() }
        root.addView(title)
        root.addView(lede)
        root.addView(btn("Connect (not implemented)") { body.text = walletStatusCopy() })
        root.addView(btn("Tape") { body.text = tapeCopy() })
        root.addView(btn("Bounties") { loadFeed() })
        root.addView(btn("Fund (not implemented)") { fund() })
        root.addView(body)
        setContentView(root)
    }

    private fun btn(label: String, click: () -> Unit) = Button(this).apply {
        text = label
        setOnClickListener { click() }
    }

    private fun walletStatusCopy(): String {
        val vault = if (wallet.seedVaultPresent) "Seed Vault detected" else "Seed Vault not detected on this device"
        return "Wallet disconnected.\nConnection and signing are not implemented in this spike.\n$vault"
    }

    private fun tapeCopy(): String {
        val rows = tape.entries()
        if (rows.isEmpty()) return "Nothing on the tape."
        return rows.joinToString("\n") { it.line }
    }

    private fun loadFeed() {
        body.text = "Loading…"
        io.execute {
            val text = try {
                val feed = FeedClient.fetch()
                if (feed.empty) "No funded bounties right now."
                else feed.listings.joinToString("\n") { it.optString("title", "bounty") }
            } catch (_: Exception) {
                "Feed paused"
            }
            runOnUiThread { body.text = text }
        }
    }

    private fun fund() {
        io.execute {
            try {
                val result = FundAction(wallet).fund("pocket-1")
                runOnUiThread {
                    body.text = "Transaction returned a signature, but confirmation is not implemented.\n${result.signature}"
                }
            } catch (_: UnsupportedOperationException) {
                runOnUiThread {
                    body.text = "Wallet connection and signing are not implemented in this spike."
                }
            } catch (_: Exception) {
                runOnUiThread { body.text = "Wallet request failed or was rejected." }
            }
        }
    }

    override fun onDestroy() {
        io.shutdownNow()
        super.onDestroy()
    }
}
