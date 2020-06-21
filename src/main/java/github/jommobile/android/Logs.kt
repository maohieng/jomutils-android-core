package github.jommobile.android.core

import android.util.Log

object Logs {
    private const val LOG_PREFIX = "jom_"
    private const val LOG_PREFIX_LENGTH = LOG_PREFIX.length

    /**
     * IllegalArgumentException is thrown if the tag.length() > 23
     * for Nougat (7.0) releases (API <= 23) and prior, there is no
     * tag limit of concern after this API level.
     */
    private const val MAX_LOG_TAG_LENGTH = 23
    fun makeTag(str: String): String {
        return if (str.length > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            LOG_PREFIX + str.substring(
                0,
                MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1
            )
        } else LOG_PREFIX + str
    }

    /**
     * Don't use this when obfuscating class names!
     */
    fun makeTag(cls: Class<*>): String {
        return makeTag(cls.simpleName)
    }

    fun D(tag: String?, message: String?) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(
                tag,
                Log.DEBUG
            )
        ) {
            Log.d(tag, message)
        }
    }

    fun D(tag: String?, message: String?, cause: Throwable?) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(
                tag,
                Log.DEBUG
            )
        ) {
            Log.d(tag, message, cause)
        }
    }

    fun V(tag: String?, message: String?) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(
                tag,
                Log.VERBOSE
            )
        ) {
            Log.v(tag, message)
        }
    }

    fun V(tag: String?, message: String?, cause: Throwable?) {
        // noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(
                tag,
                Log.VERBOSE
            )
        ) {
            Log.v(tag, message, cause)
        }
    }

    fun I(tag: String?, message: String?) {
        Log.i(tag, message)
    }

    fun I(tag: String?, message: String?, cause: Throwable?) {
        Log.i(tag, message, cause)
    }

    fun W(tag: String?, message: String?) {
        Log.w(tag, message)
    }

    fun W(tag: String?, message: String?, cause: Throwable?) {
        Log.w(tag, message, cause)
    }

    fun E(tag: String?, message: String?) {
        Log.e(tag, message)
    }

    fun E(tag: String?, message: String?, cause: Throwable?) {
        Log.e(tag, message, cause)
    }
}