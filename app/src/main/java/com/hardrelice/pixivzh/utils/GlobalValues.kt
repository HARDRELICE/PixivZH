package com.hardrelice.pixivzh.utils

import android.app.Activity
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.SystemClock
import android.view.View
import com.hardrelice.pixivzh.HttpsUtil
import com.hardrelice.pixivzh.R
import java.net.URL
import javax.net.ssl.HttpsURLConnection


fun pixiv_host() = preference.getString("pixiv_host","210.140.92.186").orEmpty()
fun pximg_host() = preference.getString("pximg_host","210.140.92.143").orEmpty()
val pixiv_headers = mapOf(
    "user-agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3298.3 Safari/537.36",
    "host" to "www.pixiv.net",
    "referer" to "https://www.pixiv.net"
)
val cookie = "first_visit_datetime_pc=2021-02-04+11%3A45%3A36; p_ab_id=3; p_ab_id_2=3; p_ab_d_id=791773885; yuid_b=Igdlg2Q; __utmc=235335808; __utmz=235335808.1612406815.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _ga=GA1.2.782120525.1612406815; c_type=17; a_type=0; b_type=1; ki_r=; login_ever=yes; __utmv=235335808.|2=login%20ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=37265945=1^9=p_ab_id=3=1^10=p_ab_id_2=3=1^11=lang=zh=1; adr_id=ZdxUdNDyXE9yckpVvERO7Wr1pjGh67PHfRBB02wsfvXamVrs; PHPSESSID=37265945_xQ2zJdKQN6u3O3ZcbwkJ7e24wK2JMwBQ; privacy_policy_agreement=2; __gads=ID=f63c7d596c9433c9-22b14acb0cc70079:T=1617676426:RT=1617676426:S=ALNI_MZqVInZp0Q1WY7t4FYpMLIfL9IJgg; tag_view_ranking=Lt-oEicbBr~-StjcwdYwv~jk9IzfjZ6n~0BLW8_65PG~jH0uD88V6F~CRVEcqljkk~M0dmgnEvWp~HffPWSkEm-~QaiOjmwQnI~azESOjmQSV~KN7uxuR89w~GFExx0uFgX~fC61ReZY41~DpPiiFjq8v~EmrEsH-TvE~JtNpyEn41H~ahkcXTb4Kg~KohghCZQvB~mHukPa9Swj~9ODMAZ0ebV~ptyAET71lu~Ie2c51_4Sp~OH5ieNjgYI~ClLaegOm3j~Wxk4MkYNNf~TGBEH-eoec~S1duJvaOGg~_pwIgrV8TB~qtVr8SCFs5~RTJMXD26Ak~v3nOtgG77A~NBK37t_oSE~LX3_ayvQX4~G_p9pkIx0Z~j3f-G5L9zb~TUVB-oxjcR~PvCsalAgmW; privacy_policy_notification=0; __utma=235335808.782120525.1612406815.1619534123.1623376832.25; ki_t=1612406870043%3B1623376833871%3B1623376857436%3B11%3B36; __utmb=235335808.2.10.1623376832; ki_s=214027%3A0.0.0.0.2%3B214908%3A0.0.0.0.2%3B214994%3A0.0.0.0.2%3B215190%3A0.0.0.0.2%3B215341%3A0.0.0.0.0%3B216591%3A0.0.0.0.2"

lateinit var handler: UIHandler
lateinit var screenSize:List<Int>
lateinit var preference: SharedPreferences

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

