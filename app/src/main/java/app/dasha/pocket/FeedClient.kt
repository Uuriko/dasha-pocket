package app.dasha.pocket

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

const val LIVE_FEED_URL = "https://www.getdasha.com/bounties.json"
const val FEED_SCHEMA = "dasha-bounties-feed/v1"

data class BountyFeed(
    val schema: String,
    val listings: List<JSONObject>,
    val note: String?,
    val url: String?,
) {
    val empty: Boolean get() = listings.isEmpty()
}

object FeedClient {
    fun parse(json: String): BountyFeed {
        val root = JSONObject(json)
        val schema = root.optString("schema")
        val listings = root.optJSONArray("listings")
        val rows = mutableListOf<JSONObject>()
        if (listings != null) {
            for (i in 0 until listings.length()) {
                rows += listings.getJSONObject(i)
            }
        }
        return BountyFeed(
            schema = schema,
            listings = rows,
            note = root.optString("note").ifEmpty { null },
            url = root.optString("url").ifEmpty { null },
        )
    }

    fun fetch(url: String = LIVE_FEED_URL): BountyFeed {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.setRequestProperty("Accept", "application/json")
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.inputStream.bufferedReader().use { reader ->
            return parse(reader.readText())
        }
    }
}
