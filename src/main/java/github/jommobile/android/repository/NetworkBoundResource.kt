package github.jommobile.android.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import github.jommobile.android.core.AppExecutors
import github.jommobile.android.repository.Resource.Factory.error
import github.jommobile.android.repository.Resource.Factory.loading
import github.jommobile.android.repository.Resource.Factory.success
import java.util.*

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * @author MAO Hieng on 1/8/19.
 */
abstract class NetworkBoundResource<RESULT, NR> @MainThread constructor(
    appExecutors: AppExecutors?
) : DataBoundResource<RESULT>(appExecutors!!) {
    override fun bind(): BoundResource<RESULT?> {
        return bind(object : DatabaseObserver<RESULT?> {
            override fun onChanged(dbSource: LiveData<RESULT?>, dbData: RESULT?) {
                if (shouldFetch(dbData)) {
                    fetchFromNetwork(dbSource)
                } else {
                    result.addSource(
                        dbSource
                    ) { result: RESULT? ->
                        setValue(
                            success(result)
                        )
                    }
                }
            }
        })
    }

    @MainThread
    protected abstract fun shouldFetch(data: RESULT?): Boolean

    private fun fetchFromNetwork(dbSource: LiveData<RESULT?>) {
        val mainThread = appExecutors.mainThread()
        val callSource =
            createNetworkCall()
        Objects.requireNonNull(
            callSource,
            "Required a network call data source, createNetworkCall() must not return null."
        )

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { setValue(loading()) }

        result.addSource(callSource) { newNetworkSource: Resource<NR?>? ->
            if (newNetworkSource == null) return@addSource
            if (newNetworkSource.status !== Resource.Status.LOADING) {
                result.removeSource(callSource)
                result.removeSource(dbSource)
                val data = newNetworkSource.data
                if (data != null) {
                    appExecutors.diskIO().execute {
                        saveNetworkResult(data)
                        mainThread.execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(
                                loadFromDb()
                            ) { newData: RESULT? ->
                                setValue(
                                    success(
                                        newData
                                    )
                                )
                            }
                        }
                    }
                } else if (newNetworkSource.status === Resource.Status.SUCCESS) { // empty data
                    mainThread.execute {
                        // reload from disk whatever we had
                        result.addSource(
                            loadFromDb()
                        ) { newData: RESULT? ->
                            setValue(
                                success(
                                    newData
                                )
                            )
                        }
                    }
                } else { // error
                    mainThread.execute {
                        onFetchFailed(newNetworkSource.cause)
                        result.addSource(
                            dbSource
                        ) {
                            if (it == null) {
                                setValue(error(newNetworkSource.message, newNetworkSource.cause))
                            } else {
                                setValue(success(it))
                            }
                        }
                    }
                }
            }
        }
    }

    @MainThread
    protected abstract fun createNetworkCall(): LiveData<Resource<NR?>>

    @WorkerThread
    protected abstract fun saveNetworkResult(networkResult: NR?)

    @MainThread
    protected open fun onFetchFailed(cause: Throwable?) {
    }
}