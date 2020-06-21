package github.jommobile.android.repository

internal class ResourceImp<T> private constructor(override val status: Resource.Status) :
    Resource<T?> {
    override var message: String? = null
        private set
    override var data: T? = null
        private set
    override var cause: Throwable? = null
        private set

    constructor(
        status: Resource.Status,
        data: T?,
        message: String?,
        cause: Throwable?
    ) : this(status) {
        this.data = data
        this.message = message
        this.cause = cause
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val resource =
            o as ResourceImp<*>
        if (status != resource.status) {
            return false
        }
        if (if (message != null) message != resource.message else resource.message != null) {
            return false
        }
        return if (data != null) data == resource.data else resource.data == null
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + if (message != null) message.hashCode() else 0
        result = 31 * result + if (data != null) data.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

}