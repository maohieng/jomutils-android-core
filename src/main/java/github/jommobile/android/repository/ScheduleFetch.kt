package github.jommobile.android.repository

import java.util.*

class ScheduleFetch(private val timeGapSeconds: Long) :
    NetworkFetchOption {
    override fun needFetchNow(): Boolean {
        val last =
            lastFetchSeconds * 1000
        val current = System.currentTimeMillis()
        val gapSeconds =
            Math.round(((current - last) / 1000).toDouble())
        val needFetch = gapSeconds >= timeGapSeconds
        if (needFetch) {
            lastFetchSeconds =
                Math.round((current / 1000).toDouble())
        }
        return needFetch
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj !is ScheduleFetch) {
            return false
        }
        return timeGapSeconds == obj.timeGapSeconds
    }

    override fun hashCode(): Int {
        return Objects.hash(timeGapSeconds)
    }

    override fun toString(): String {
        return "NetworkFetchOption:Schedule:$timeGapSeconds sec"
    }

    companion object {
        private var lastFetchSeconds: Long = 0
    }

}