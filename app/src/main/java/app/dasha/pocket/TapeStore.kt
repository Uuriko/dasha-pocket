package app.dasha.pocket

/** Commons human kinds only. Not the /digest price tape. */
enum class TapeKind {
    created, funded, submitted, selected, paid, cancelled
}

data class TapeLine(
    val id: String,
    val kind: TapeKind,
    val line: String,
    val origin: String,
    val tx: String? = null,
    val chainObserved: Boolean = false,
)

class TapeStore {
    private val seen = linkedSetOf<String>()
    private val rows = mutableListOf<TapeLine>()

    fun entries(): List<TapeLine> = rows.toList()

    fun ingest(line: TapeLine): Boolean {
        val key = line.id
        if (key in seen) return false
        seen += key
        rows += line
        return true
    }
}
