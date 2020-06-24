package github.jommobile.android.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import github.jommobile.android.core.AppExecutors
import java.util.*

/**
 * A generic class that can provide a resource backed by the sqlite database.
 *
 * @author MAO Hieng on 1/8/19.
 */
abstract class DataBoundResource<RESULT> @MainThread constructor(//    private static final String TAG = Logs.makeTag(DataBoundResource.class);
    protected val appExecutors: AppExecutors
) {
    interface DatabaseObserver<T> {
        fun onChanged(dbSource: LiveData<T?>, dbData: T?)
    }

    protected val result: MediatorLiveData<Resource<RESULT?>?> = MediatorLiveData()

    init {
        result.value = null
    }

    protected fun setValue(newValue: Resource<RESULT?>?) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    @MainThread
    open fun bind(): BoundResource<RESULT?> {
        return bind(object : DatabaseObserver<RESULT?> {
            override fun onChanged(dbSource: LiveData<RESULT?>, dbData: RESULT?) {
                result.addSource(
                    dbSource
                ) { result: RESULT? ->
                    setValue(
                        Resource.Factory.success(result)
                    )
                }
            }
        })
    }

    @MainThread
    fun bind(observer: DatabaseObserver<RESULT?>?): BoundResource<RESULT?> {
        setValue(Resource.Factory.loading())
        val dbSource = loadFromDb()
        Objects.requireNonNull(dbSource, "loadFromDb() must not return null.")
        result.addSource(dbSource) { data: RESULT? ->
            result.removeSource(dbSource)
            observer?.onChanged(dbSource, data)
        }
        return object : BoundResource<RESULT?> {
            override fun asLiveData(): LiveData<Resource<RESULT?>?> {
                return result
            }
        }
    }

    @MainThread
    protected abstract fun loadFromDb(): LiveData<RESULT?>

}