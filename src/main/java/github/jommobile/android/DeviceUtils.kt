package github.jommobile.android

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.Settings.Secure
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DimenRes
import java.util.*

object DeviceUtils {
    /**
     * Giving the app's package.
     */
    const val PLAY_STORE_APP_URL_FORMAT =
        "https://play.google.com/store/apps/details?id=%s"
    private const val MARKET_APP_DETAIL_FORMAT = "market://details?id=%s"

    /**
     * Returns true if the running device is tablet.
     *
     * @param context
     * @return
     */
    fun isTablet(context: Context): Boolean {
        return context.resources
            .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun getDeviceId(context: Context): String {
        // https://android-developers.googleblog.com/2011/03/identifying-app-installations.html
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    fun getLanguage(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    fun changeLocaleConfiguration(
        context: Context,
        newLocale: Locale?
    ) {
        val res = context.resources
        // Change locale setting in app
        val dm = res.displayMetrics
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(newLocale)
        } else {
            conf.locale = newLocale
        }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(conf)
        } else {
            res.updateConfiguration(conf, dm)
        }
    }

    /**
     * Returns does the device network have connection to WIFI or Mobile.
     *
     * @param context : application context
     * @return true if the device connects to WIFI or Mobile, and vice versa.
     */
    fun isOnline(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return if (netInfo != null && netInfo.isConnectedOrConnecting) {
            true
        } else false
    }

    /**
     * Returns does the device network is a WIFI connection.
     *
     * @param context
     * @return
     */
    fun isWiFiConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return if (info != null && info.type == ConnectivityManager.TYPE_WIFI && info.isConnected) {
            true
        } else false
    }

    fun isMobileDataConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return if (info != null && info.isConnected) {
            when (info.type) {
                ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_MMS, ConnectivityManager.TYPE_MOBILE_SUPL, ConnectivityManager.TYPE_MOBILE_DUN, ConnectivityManager.TYPE_MOBILE_HIPRI -> true
                else -> false
            }
        } else false
    }
    //    /**
    //     * Returns current connected WiFi's SSID (Service Set Identifider). May be <code>null</code>.</br> <b>Note:</b> You must check
    //     * network or WiFi connection first by calling {@link #isWiFiConnected(Context)} or {@link #isOnline(Context)}
    //     *
    //     * @param context
    //     * @return
    //     */
    //    public static String getWiFiSSID(Context context) {
    //        String ssid = null;
    //        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    //        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    //        if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())) {
    //            ssid = wifiInfo.getSSID();
    //        }
    //
    //        return ssid;
    //    }
    /**
     * Returns the application version name of the application's `context` given.
     *
     * @param context : application context
     * @return the context's application version name which is set in Manifest file.
     */
    fun getAppVersionName(context: Context): String {
        var appVersionName = ""
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersionName
    }

    /**
     * Returns the application version code of the application's `context` given.
     *
     * @param context : application context
     * @return the context's application version code which is set in Manifest file.
     */
    fun getAppVersionCode(context: Context): Int {
        return try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            // should never happen
            throw RuntimeException("Could not get package name: $e")
        }
    }

    /**
     * Returns a full device name (model and manufacturer). E.g. Samsung GT-S5830L, Motorola MB860, Sony Ericsson LT18i, LGE
     * LG-P500, HTC Desire V, HTC Wildfire S A510e ...
     *
     * @return
     */
    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalizeFirstChar(model)
            } else {
                capitalizeFirstChar(
                    manufacturer
                ) + " " + model
            }
        }

    fun capitalizeFirstChar(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    val sDKCodeName: String
        get() {
            var codeName = ""
            val fields = VERSION_CODES::class.java.fields
            for (field in fields) {
                val fieldName = field.name
                var fieldValue = -1
                try {
                    fieldValue = field.getInt(Any())
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                if (fieldValue == Build.VERSION.SDK_INT) {
                    codeName = fieldName
                }
            }
            return codeName
        }

    private fun getScreenSize(windowManager: WindowManager): Point {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    /**
     * Returns the device's screen size which its width and height is in pixel.
     *
     * @param activity : Current [Activity]
     * @return a [Point]
     */
    fun getScreenSize(activity: Activity): Point {
        val wm = activity.windowManager
        return getScreenSize(wm)
    }

    /**
     * Returns the device's screen size which its width and height is in pixel.
     *
     * @param context : application context
     * @return a [Point]
     */
    fun getScreenSize(context: Context): Point {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return getScreenSize(wm)
    }

    fun getPixelSizeFromDp(context: Context, @DimenRes id: Int): Float {
        val resources =
            context.applicationContext.resources
        val displayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            resources.getDimension(id),
            displayMetrics
        )
    }

    /**
     * Convenience method to force dismissing the keyboard.
     *
     * @param editText
     */
    fun dismissKeyboard(editText: EditText) {
        val imm = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * Convenience method to force showing the keyboard.
     *
     * @param editText
     */
    fun showKeyboard(editText: EditText) {
        val im = editText.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, 0)
    }

    fun dismissKeyboard(activity: Activity) {
        val im =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            im.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    fun createDialIntent(phone: String): Intent {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        return intent
    }

    fun createShareChooserIntent(title: String?, sharedText: String?): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, sharedText)
        return Intent.createChooser(intent, title)
    }

    fun startPlayStoreAppDetail(context: Context) {
        val appPackageName = context.packageName
        var intent: Intent
        try {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    String.format(
                        Locale.US,
                        MARKET_APP_DETAIL_FORMAT,
                        appPackageName
                    )
                )
            )
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    String.format(
                        Locale.US,
                        PLAY_STORE_APP_URL_FORMAT,
                        appPackageName
                    )
                )
            )
            context.startActivity(intent)
        }
    }
}