package github.jommobile.android.repository

object FetchOptions {
    fun always(): AlwaysFetch {
        return AlwaysFetch()
    }

    fun schedule(timeGapSeconds: Long): ScheduleFetch {
        return ScheduleFetch(timeGapSeconds)
    }

    fun never(): NeverFetch {
        return NeverFetch()
    }
}