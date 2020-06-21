package github.jommobile.android

import android.content.BroadcastReceiver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

object ConnectionUtils {

    @Suppress("DEPRECATION")
    fun isConnectionAvailable(context: Context): Boolean {
        // https://stackoverflow.com/a/53532456/857346
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            result = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun checkInternetConnection(context: Context, pendingResult: BroadcastReceiver.PendingResult?) : LiveData<Boolean> {
        val hasConnection =
            isConnectionAvailable(
                context
            )
        if (hasConnection) {
            val checker =
                InternetChecker(
                    pendingResult
                )
            checker.execute()
            return checker.liveData
        } else {
            return MutableLiveData(false);
        }
    }

    class InternetChecker(
        var pendingResult: BroadcastReceiver.PendingResult?
    ) : AsyncTask<Void, Int, Boolean>() {

        val liveData: MutableLiveData<Boolean> = MutableLiveData()

        override fun doInBackground(vararg params: Void?): Boolean {
            return connectGoogleDNS();
        }

        private fun connectGoogleDNS(): Boolean {
            // https://stackoverflow.com/a/27312494
            return try {
                val timeoutMs = 1500
                val sock = Socket()
                val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockaddr, timeoutMs)
                sock.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            liveData.value = result
            pendingResult?.finish()
        }
    }

}