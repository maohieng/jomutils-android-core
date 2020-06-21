package github.jommobile.android.repository

class AlwaysFetch : NetworkFetchOption {
    override fun needFetchNow(): Boolean {
        return true
    }

    override fun equals(obj: Any?): Boolean {
        return if (this === obj) true else obj is AlwaysFetch && obj.needFetchNow()
    }

    override fun hashCode(): Int {
        return 31 + super.hashCode()
    }

    override fun toString(): String {
        return "NetworkFetchOption:Always"
    }
}