package github.jommobile.android.core

import androidx.lifecycle.LiveData

/**
 * Created by MAO Hieng on 9/14/17.
 *
 *
 * A LiveData class that have `null` value.
 *
 */
class AbsentLiveData<T> private constructor() : LiveData<T>() {
    companion object {
        fun <T> create(): AbsentLiveData<T> {
            return AbsentLiveData()
        }
    }

    init {
        postValue(null)
    }
}