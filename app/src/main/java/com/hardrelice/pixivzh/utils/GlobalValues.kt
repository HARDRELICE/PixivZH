package com.hardrelice.pixivzh.utils

import android.app.Activity
import android.content.ContextWrapper
import android.os.SystemClock
import android.view.View
import com.hardrelice.pixiver.UIHandler
import com.hardrelice.pixivzh.HttpsUtil
import com.hardrelice.pixivzh.R
import java.net.URL
import javax.net.ssl.HttpsURLConnection


val pixiv_host = "210.140.131.203"
val pximg_host = "210.140.92.138"
val pixiv_headers = mapOf(
    "User-Agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3298.3 Safari/537.36",
    "Host" to "www.pixiv.net",
    "referer" to "https://www.pixiv.net".encode()
)

lateinit var handler: UIHandler
lateinit var screenSize:List<Int>

fun String.encode(encode: String = "utf-8")=java.net.URLEncoder.encode(this, encode)

fun String.addParams(params: HashMap<String, Any?>):String{
    var str = this
    for(key in params.keys){
        if (params[key]!=null) {
            str += "$key=${params[key]}&"
        }
    }
    return  str
}

fun Int.px2dp(): Float {
    val density = ApplicationUtil.ApplicationContext!!.resources.displayMetrics.density
    return (this / density +0.5f)
}

fun Int.dp2px(): Float {
    val density = ApplicationUtil.ApplicationContext!!.resources.displayMetrics.density
    return (this * density + 0.5f)
}

fun Activity.screenSize():List<Int>{
//    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//        val bounds = this.windowManager.currentWindowMetrics.bounds
//        listOf(bounds.width(),bounds.height())
//    } else {
//        val display = this.windowManager.defaultDisplay
//        listOf(display.width,display.height)
//    }
    return listOf(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
}

fun Int.getColor():Int{
    val resources = ApplicationUtil.ApplicationContext!!.resources
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        resources.getColor(this, ApplicationUtil.ApplicationContext!!.theme)
    } else {
        resources.getColor(this)
    }
}

fun Int.setStatusBarColor(activity: Activity){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        activity.window.statusBarColor = activity.resources.getColor(
            this,
            activity.theme
        )
    } else {
        activity.window.statusBarColor = activity.resources.getColor(this)
    }
}

fun String.openVerifiedConnection(headers: Map<String, String> = pixiv_headers, params: HashMap<String, Any?> = hashMapOf()): HttpsURLConnection{
    HttpsUtil.trustEveryone()
    val connection = URL(this.addParams(params)).openConnection() as HttpsURLConnection
    for(key in headers.keys){
        connection.setRequestProperty(key,headers[key])
    }
    connection.setHostnameVerifier { hostname, session ->  true}
    connection.connectTimeout = 30000
    connection.readTimeout = 30000
    return connection
}

fun getActivity(view: View): Activity? {
    var context = view.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun View.onSingleClick(
    listener: (View) -> Unit,
    interval: Int = 500,
    isShareSingleClick: Boolean = true

) {
    setOnClickListener {
        val target = if (isShareSingleClick) getActivity(this)?.window?.decorView ?: this else this
        val millis = target.getTag(R.id.single_click_tag_last_single_click_millis) as? Long ?: 0
        if (SystemClock.uptimeMillis() - millis >= interval) {
            target.setTag(
                R.id.single_click_tag_last_single_click_millis, SystemClock.uptimeMillis()
            )
            listener.invoke(this)
        }
    }
}