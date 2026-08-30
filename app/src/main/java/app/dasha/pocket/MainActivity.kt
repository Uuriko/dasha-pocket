package app.dasha.pocket

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

/**
 * Native screens. Not a WebView of getdasha.com.
 * Browse Tape + feed with no wallet. Fund signs only after tap.
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
        body = TextView(this).apply { text = connectCopy() }
        root.addView(title)
        root.addView(lede)
        root.addView(btn("Connect") { body.text = connectCopy() })
        root.addView(btn("Tape") { body.text = tapeCopy() })
        root.addView(btn("Bounties") { loadFeed() })
        root.addView(btn("Fund") { fund() })
        root.addView(body)
        setContentView(root)
    }

    private fun btn(label: String, click: () -> Unit) = Button(this).apply {
        text = label
        setOnClickListener { click() }
    }

    private fun connectCopy(): String {
        val vault = if (wallet.seedVaultPresent) "Seed Vault present" else "Seed Vault not on this device"
        return "Wallet off until you tap.\n$vault"
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
                tape.ingest(
                    TapeLine(
                        id = "funded:pocket-1:${result.signature}",
                        kind = TapeKind.funded,
                        line = "funded bounty #1",
                        origin = "app",
                        tx = result.signature,
                    ),
                )
                runOnUiThread { body.text = "Funded" }
            } catch (_: UnsupportedOperationException) {
                runOnUiThread { body.text = "Wallet" }
            } catch (_: Exception) {
                runOnUiThread { body.text = "Rejected" }
            }
        }
    }
}
