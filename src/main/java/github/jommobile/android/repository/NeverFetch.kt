package github.jommobile.android.repository

/**
 * This class is used for ...
 *
 * @autor MAO Hieng 7/19/2019
 */
class NeverFetch : NetworkFetchOption {
    override fun needFetchNow(): Boolean {
        return false
    }

    override fun equals(obj: Any?): Boolean {
        return if (this === obj) true else obj is NeverFetch && !obj.needFetchNow()
    }

    override fun hashCode(): Int {
        return 31 + super.hashCode()
    }
}