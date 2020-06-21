package github.jommobile.android.repository

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 * @author MAO Hieng on 1/8/19.
</T> */
interface Resource<T> {
    /**
     * Status of a resource that is provided to the UI.
     *
     *
     * These are usually created by the Repository classes where they return
     * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
     */
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    object Factory {
        fun <T> success(data: T?): Resource<T?> {
            return ResourceImp(
                Status.SUCCESS,
                data,
                null,
                null
            )
        }

        fun <T> loading(): Resource<T?> {
            return ResourceImp(
                Status.LOADING,
                null,
                null,
                null
            )
        }

        fun <T> error(
            message: String?,
            cause: Throwable?
        ): Resource<T?> {
            return ResourceImp(
                Status.ERROR,
                null,
                message,
                cause
            )
        }
    }

    val status: Status
    val message: String?
    val cause: Throwable?
    val data: T?
}