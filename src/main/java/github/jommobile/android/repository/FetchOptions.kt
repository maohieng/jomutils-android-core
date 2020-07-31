package github.jommobile.android.repository

object FetchOptions {
    fun always(): AlwaysFetchOption {
        return AlwaysFetchOption()
    }

    fun schedule(timeGapMillis: Long): ScheduleFetchOption {
        return ScheduleFetchOption(timeGapMillis)
    }

    fun never(): NeverFetchOption {
        return NeverFetchOption()
    }
}