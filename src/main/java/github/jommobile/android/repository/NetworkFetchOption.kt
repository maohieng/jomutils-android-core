package github.jommobile.android.repository

import androidx.annotation.MainThread

interface NetworkFetchOption {
    @MainThread
    fun needFetchNow(): Boolean
}