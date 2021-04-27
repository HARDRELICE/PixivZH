package com.hardrelice.pixivzh.utils

import android.util.Log
import com.google.gson.Gson
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.FileHandler.checkDir
import com.hardrelice.pixivzh.FileHandler.getIllustFolder
import com.hardrelice.pixivzh.ui.main.datatype.CommonItem
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import com.hardrelice.pixivzh.ui.main.datatype.SearchItem
import com.hardrelice.pixivzh.ui.main.datatype.SearchSetting
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.android.awaitFrame
//import kotlinx.coroutines.awaitAll
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeoutException
import kotlin.collections.HashMap

object Pixiv {
    // Urls data class, 简易的url调取
    data class Urls(
        var mini: String = "",
        var thumb: String = "",
        var small: String = "",
        var regular: String = "",
        var original: String = "",
        var rank: String = ""
    )

    data class MangaUrls(
        var mini: MutableList<String> = mutableListOf<String>(),
        var thumb: MutableList<String> = mutableListOf<String>(),
        var small: MutableList<String> = mutableListOf<String>(),
        var regular: MutableList<String> = mutableListOf<String>(),
        var original: MutableList<String> = mutableListOf<String>()
    )

    // IllustInfo data class, 简易的info调取
    data class IllustInfo(
        var illustId: String = "",
        var id: String = "",
        var illust_id: Int = 0,
        var illustTitle: String = "",
        var title: String = "",
        var date: String = "",
        var alt: String = "",
        var illustComment: String = "",
        var createDate: String = "",
        var updateDate: String = "",
        var xRestrict: String = "",
        var userId: String = "",
        var user_id: String = "",
        var userName: String = "",
        var user_name: String = "",
        var userAccount: String = "",
        var profileImageUrl: String = "",
        var profile_img: String = "",
        var url: String = "",
        var urls: Urls? = null,
        var tags_: MutableList<String> = mutableListOf(),
        var width: Int = 0,
        var height: Int = 0,
        var pageCount: Int = 0,
        var illust_page_count: String = "",
        var bookmarkCount: Int = 0,
        var likeCount: Int = 0,
        var viewCount: Int = 0,
        var rank: Int = 0,
        var yes_rank: Int = 0,
        var rating_count: Int = 0,
        var view_count: Int = 0
//        val :String = "",
    )

    data class TagInfo(
        var abstract:String = "",
        var id:String = "",
        var image:String = ""
    )

    // 通过thumbUrl(mini, thumb, small中任意一种)生成所有Urls
    fun generateUrlsFromThumbUrl(url: String): Urls {
//        https://i.pximg.net/c/250x250_80_a2/img-master/img/2021/01/26/20/07/56/87320495_p0_square1200.jpg
//        val url = "https://i.pximg.net/c/250x250_80_a2/img-master/img/2021/01/26/20/07/56/87320495_p0_square1200.jpg"
        val pat = Regex("img/(.*)_square1200.jpg")
        val pat2 = Regex("img/(.*)_master1200.jpg")
        var ret = pat.find(url)
        if (ret == null) {
            ret = pat2.find(url)
        }
        if (ret != null) {
            val str = ret!!.groupValues[1]
            val urls = Urls(
                mini = "https://i.pximg.net/c/48x48/img-master/img/${str}_square1200.jpg",
                thumb = "https://i.pximg.net/c/250x250_80_a2/img-master/img/${str}_square1200.jpg",
                small = "https://i.pximg.net/c/540x540_70/img-master/img/${str}_master1200.jpg",
                regular = "https://i.pximg.net/img-master/img/${str}_master1200.jpg",
                original = "https://i.pximg.net/img-original/img/${str}.jpg",
                rank = "https://i.pximg.net/c/240x480/img-master/img/${str}_master1200.jpg"
            )
            return urls
        }
        return Urls()
    }

    fun generateMangaUrlsFromThumbUrl(url: String, pageCount: Int): MangaUrls {
        val urls = generateUrlsFromThumbUrl(url)
        val mangaUrls: MangaUrls = MangaUrls()

        for (i in 0 until pageCount) {
            mangaUrls.mini.add(urls.mini.replace("p0", "p${pageCount.toString()}"))
            mangaUrls.thumb.add(urls.thumb.replace("p0", "p${pageCount.toString()}"))
            mangaUrls.small.add(urls.small.replace("p0", "p${pageCount.toString()}"))
            mangaUrls.regular.add(urls.regular.replace("p0", "p${pageCount.toString()}"))
            mangaUrls.original.add(urls.original.replace("p0", "p${pageCount.toString()}"))
        }
        println(mangaUrls)
        return mangaUrls
    }

    fun getUserInfo(uid: String) {

    }


    // 储存成功获取的json
    fun saveIllustInfo(pid: String, data: String) {
        checkDir(getIllustFolder(pid))
        File(getIllustFolder(pid, "info.json")).writeText(data)
    }

    // 从json文本中提取IllustInfo
    fun parseIllustInfo(pid: String, data: String): IllustInfo {
        val info = JSONObject(data).getJSONObject("illust").getString(pid)
        val ginfo = Gson().fromJson(info, IllustInfo::class.java)
        val jarr: JSONArray = JSONObject(info).getJSONObject("tags").getJSONArray("tags")
        for (j in 0 until jarr.length()) {
            val str = jarr.get(j) as JSONObject
            ginfo.tags_.add(str.getString("tag"))
        }
        println(ginfo)
        return ginfo
    }

    // 从缓存的json中获取IllustInfo
    fun getIllustInfoFromLocal(pid: String): IllustInfo? {
        val data = File(FileHandler.getIllustFolder(pid, "info.json")).readText()
        return parseIllustInfo(pid, data)
    }

    // 获取IllustInfo，自动检查是否缓存
    fun getIllustInfo(pid: String): IllustInfo? {
        if (File(FileHandler.getIllustFolder(pid, "info.json")).exists()) {
            return getIllustInfoFromLocal(pid)
        }
        try {
            println("???")
            val res =
                Requests.get("https://$pixiv_host/artworks/$pid", pixiv_headers) ?: return null
            val data = res.getElementById("meta-preload-data")?.attr("content") ?: return null
            saveIllustInfo(pid, data)
            println(data)
            return parseIllustInfo(pid, data)
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    // 多线程下载original图片
    fun getIllustImage(
        pid: String,
        uiHandler: UIHandler,
        progressBarId: Int,
        thumbUri: String = ""
    ): Boolean {
        val picPath = FileHandler.getIllustFolder(pid, "original.jpg")
        val picDir = FileHandler.getIllustFolder(pid)
        val tempPath = FileHandler.getIllustFolder(pid, "temp.jpg")
        if (File(picPath).exists()) {
            println("exists")
            return true
        }
        var url: String? = null
        if (thumbUri == "") {
            println("getInfo Start")
            val info = getIllustInfo(pid)
            println("getInfo End")
            url = info?.urls?.original
        } else {
            url = generateUrlsFromThumbUrl(thumbUri).original
        }
        println("url$url")
        if (url != null) {
            FileHandler.checkDir(picDir)
//            println(picPath)
            url.let {
//                Rq.download(url.replace("i.pximg.net", pximg_host), hashMapOf<String,String>("Host" to "www.pixiv.net","Referer" to "https://www.pixiv.net".encode()),picPath)
                try {
                    val x = Requests.threadDownload(
                        url.replace("i.pximg.net", pximg_host),
                        hashMapOf<String, String>(
                            "Host" to "www.pixiv.net",
                            "Referer" to "https://www.pixiv.net".encode()
                        ),
                        picPath,
                        tempPath,
                        16,
                        uiHandler,
                        progressBarId
                    )
                    if (!x) {
                        return false
                    }
                    println("/")
                    return true
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
        return false
    }

    // 多线程下载original图片
    fun getIllustImageThumb(pid: String): Boolean {
        val picDir = FileHandler.externalCacheDir()?.let { FileHandler.join(it, pid) }!!
        val picPath = FileHandler.join(picDir, "original.jpg")
        if (File(picPath).exists()) {
            println("exists")
            return true
        }
        println("getInfo Start")
        val info = getIllustInfo(pid)
        println("getInfo End")
        val url = info?.urls?.thumb
        println("url$url")
        if (url != null) {
            FileHandler.checkDir(picDir)
//            println(picPath)
            url.let {
                while (true) {
                    try {
                        Requests.download(
                            url.replace("i.pximg.net", pximg_host),
                            hashMapOf<String, String>(
                                "Host" to "www.pixiv.net",
                                "Referer" to "https://www.pixiv.net".encode()
                            ),
                            picPath
                        )
                        break
                    } catch (e: Exception) {
                        Log.e("getIllustImageThumb", e.message!!)
                    }
                }
            }
        }
        return false
    }

    fun searchTag(tagName:String, lang:String = "zh"): TagInfo?{
        val url = "https://$pixiv_host/ajax/search/tags/$tagName?lang=$lang"
        val connection = url.openVerifiedConnection()
        val tagInfo = TagInfo()
        try {
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val data = connection.inputStream.bufferedReader().readText()
            val body = JSONObject(data).getJSONObject("body")
            return try {
                val pixpedia = body.getJSONObject("pixpedia")
                tagInfo.abstract = pixpedia.getString("abstract")
                tagInfo.id = pixpedia.getString("id")
                tagInfo.image = pixpedia.getString("image")
                tagInfo
            } catch (e:Exception){
                null
            }
        } catch (e: TimeoutException) {
            handler.toast("Timeout!")
            return null
        } catch (e: Exception) {
            handler.toast("Unknown Exception!")
            return null
        }
    }

    // 关键字搜索illust
    fun search(
        word: String,
        setting: SearchSetting
    ): MutableList<SearchItem> {
        val by = setting.by
        val order = setting.order
        val mode = setting.mode
        val p = setting.p
        val s_mode = setting.s_mode
        val ratio = setting.ratio
        val type = setting.type
        val lang = setting.lang
        val url =
            "https://$pixiv_host/ajax/search/$by/$word?word=$word&order=$order&mode=$mode&p=$p&s_mode=$s_mode&ratio=$ratio&type=$type&lang=$lang"
        val connection = url.openVerifiedConnection()
        val data: String
        try {
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            data = connection.inputStream.bufferedReader().readText()
        } catch (e: TimeoutException) {
            handler.toast("Timeout!")
            return mutableListOf()
        } catch (e: Exception) {
            handler.toast("Unknown Exception!")
            return mutableListOf()
        }
//        data = Requests.getJson(url, pixiv_headers)
        val objName = when (by) {
            "artworks" -> {
                "illustManga"
            }
            "illustration" -> {
                "illust"
            }
            "manga" -> {
                "manga"
            }
            else -> ""
        }
        val obj: JSONObject
        try {
            obj = JSONObject(data)
        } catch (e: Exception) {
            handler.toast("Parse JSON Failed!")
            return mutableListOf(SearchItem())
        }
        if (obj.getBoolean("error")) {
            handler.toast("Request Information Incorrect!")
            return mutableListOf()
        }
        val info: JSONArray
        try {
            info =
                JSONObject(data).getJSONObject("body").getJSONObject(objName).getJSONArray("data")
            println(info)
        } catch (e: Exception) {
            handler.toast("Request Information Incorrect!")
            return mutableListOf()
        }
        val items = mutableListOf<SearchItem>()
        for (i in 0 until info.length()) {
            val x = parseIllustInfoShort(info[i].toString())
            items.add(
                SearchItem(
                    illustId = x.illust_id.toString(),
                    title = x.title,
                    userId = x.user_id,
                    userName = x.user_name,
                    profileImg = x.profile_img,
                    thumbUrl = x.url,
                    width = x.width,
                    height = x.height,
                    tags = x.tags_ as ArrayList<String>,
                    pageCount = x.illust_page_count.toInt()
                )
            )
        }
//        val ginfo = Gson().fromJson(info, IllustInfo::class.java)
//        val jarr:JSONArray = JSONObject(info).getJSONObject("tags").getJSONArray("tags")
        return items
    }

    fun getRank(
        date: String? = null,
        mode: String = "daily",
        content: String? = null,
        p: Int = 1
    ): MutableList<RankItem> {
        val params: HashMap<String, Any?> = hashMapOf(
            "mode" to mode,
            "p" to p,
            "format" to "json"
        )
        if (date != null) {
            params["date"] = date
        }
        if (content != null) {
            params["content"] = content
        }
        println(params)

        val url = "https://$pixiv_host/ranking.php?".addParams(params)
        val connection = url.openVerifiedConnection()
        val res: String
        try {
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            res = connection.inputStream.bufferedReader().readText()
            println(res)
        } catch (e: TimeoutException) {
            handler.toast("Timeout!")
            return mutableListOf()
        } catch (e: Exception) {
            println(e.message)
            handler.toast("Unknown Exception!")
            return mutableListOf()
        }

//        try {
//            res = Requests.getJson(url, pixiv_headers, params) ?: return mutableListOf()
//        } catch (e: Exception) {
//            println(e.message)
//            return mutableListOf()
//        }

        val rankList: MutableList<RankItem> = mutableListOf()
        try {
            val info = JSONObject(res).getJSONArray("contents")
            for (i in 0 until info.length()) {
                val obj = info[i] as JSONObject
                val x = parseIllustInfoShort(obj.toString())
                val item = RankItem(
                    illustId = x.illust_id.toString(),
                    title = x.title,
                    rank = "#${x.rank}",
                    userId = x.user_id,
                    userName = x.user_name,
                    profileImg = x.profile_img,
                    thumbUrl = x.url,
                    width = x.width,
                    height = x.height,
                    tags = x.tags_ as ArrayList<String>,
                    pageCount = x.illust_page_count.toInt()
                )
                if (x.yes_rank != 0) {
                    item.rank += " pre#${x.yes_rank}"
                } else {
                    item.rank += " new!"
                }
                rankList.add(item)
            }
        } catch (e: Exception) {
            handler.toast("Unknown Exception!")
            Log.e("getRank", e.message!!)
        }
        if (rankList.size != 0) {
            return rankList
        }
        return mutableListOf()
    }

    fun parseIllustInfoShort(data: String): IllustInfo {
        val ginfo = Gson().fromJson(data, IllustInfo::class.java)
        val jarr: JSONArray = JSONObject(data).getJSONArray("tags")
        for (j in 0 until jarr.length()) {
            val str = jarr.get(j)
            ginfo.tags_.add(str.toString())
        }
        println(ginfo)
        return ginfo
    }

    fun getRecommend(pid: String, limit: Int): List<CommonItem> {
        val url = "https://www.pixiv.net/ajax/illust/$pid/recommend/init?limit=$limit"
        var res = ""
        try {
            res = Requests.getJson(url, pixiv_headers) ?: return mutableListOf()
        } catch (e: Exception) {
            println(e.message)
            return mutableListOf()
        }
        val items = mutableListOf<CommonItem>()
        val info = JSONObject(res).getJSONObject("body").getJSONArray("illusts")
        for (index in 0 until info.length()) {
            val obj = info[index] as JSONObject
            try {
                val x = parseIllustInfoShort(obj.toString())
                items.add(
                    CommonItem(
                        illustId = x.id.toString(),
                        title = x.title,
                        userId = x.userId,
                        userName = x.userName,
                        profileImg = x.profileImageUrl,
                        thumbUrl = x.url,
                        width = x.width,
                        height = x.height,
                        tags = x.tags_ as ArrayList<String>,
                        pageCount = x.pageCount
                    )
                )
            } catch (e: Exception) {
                println(obj.toString())
            }

        }
        return items
    }

}
