package github.jommobile.android.repository

import androidx.lifecycle.LiveData

/**
 * This class is used for ...
 *
 * @autor MAO Hieng 7/19/2019
 */
interface BoundResource<C> {
    fun asLiveData(): LiveData<Resource<C>?>?
}