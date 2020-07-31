package github.jommobile.android.repository

class ScheduleFetchOption(private val timeGapMillis: Long) :
    NetworkFetchOption {

    override fun needFetchNow(): Boolean {
        val current = System.currentTimeMillis()
        val remainGap = current - lastFetchMillis
        val fetchNow = remainGap >= timeGapMillis
        if (fetchNow) {
            lastFetchMillis = current
        }

        return fetchNow
    }

    override fun toString(): String {
        return "NetworkFetchOption:Schedule:$timeGapMillis millisec"
    }

    companion object {
        private var lastFetchMillis: Long = 0L
    }

}