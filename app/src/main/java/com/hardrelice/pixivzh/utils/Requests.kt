package com.hardrelice.pixivzh.utils

import android.os.Message
import android.util.Log
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixivzh.HttpsUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object Requests {

    data class Progress(
        var done: Int = 0,
        var total: Int = 0,
        var lastUpdateTime:Long = 0L,
    )

    fun get(
        url: String,
        headers: Map<String, String>,
        params: HashMap<String, Any?> = hashMapOf()
    ): Document? {
        HttpsUtil.trustEveryone()
        var res: Document? = null
        var url = url
        for (key in params.keys) {
            url = "$url&$key=${params[key].toString()}"
        }
        try {
            val raw = Jsoup.connect(url).headers(headers).timeout(10000).execute()
            val statusCode = raw.statusCode()
            println(statusCode)
            try {
                res = raw.parse()
            } catch (e:Exception){
                res = null
            }
            println(res)
        } catch (e: Exception) {
            println(e.message)
        }
        return res
    }

    fun getJson(
        url: String,
        headers: Map<String, String>,
        params: HashMap<String, Any?> = hashMapOf()
    ): String? {
        HttpsUtil.trustEveryone()
        var url = url
        for (key in params.keys) {
            Log.e("key", "$key ${params[key]}")
            url = "$url&$key"
        }
        Log.e("getJson", url)
        var res:String? = ""
        try {
            res = Jsoup.connect(url).headers(headers).ignoreContentType(true).execute().body() ?: null
        }catch (e:Exception){
            Log.e("getJson",e.message!!)
        }
        return res
    }

    fun call(url: String, headers: Map<String, String>): String {
        HttpsUtil.trustEveryone()
        val conn = URL(url).openConnection() as HttpsURLConnection
        conn.setRequestProperty("Referer", "https://www.pixiv.net")
        conn.setRequestProperty("Host", "www.pixiv.net")
        conn.setHostnameVerifier { _, session -> true }
        val content = conn.content
        println(content.toString())
        return content.toString()
    }

    fun download(url: String, properties: Map<String, String>, filePath: String) {
        while (true) {
            HttpsUtil.trustEveryone()
//        SSLContextSecurity.createIgnoreVerifySSL("SSL")
            val conn = URL(url).openConnection() as HttpsURLConnection
            for (key in properties) {
                conn.setRequestProperty(key.toString(), properties[key.toString()].toString())
            }
            conn.setRequestProperty("Referer", "https://www.pixiv.net")
            conn.setHostnameVerifier { _, session -> true }
            conn.readTimeout = 10000
            conn.connectTimeout = 10000
            try {
                conn.connect()
                conn.inputStream.use { input ->
                    BufferedOutputStream(FileOutputStream(filePath)).use { output ->
                        input.copyTo(output) //将文件复制到本地 其中copyTo使用方法可参考我的Io流笔记
                    }
                }
                break
            } catch (e: Exception) {
                Log.e("download", e.message!!)
            } finally {
                conn?.disconnect()
            }
        }
    }

    fun mdownload(url: String, properties: HashMap<String, String>, filePath: String) {
        HttpsUtil.trustEveryone()
        val conn = URL(url).openConnection() as HttpsURLConnection
        var fos: FileOutputStream? = null
        val buffer = ByteArray(1024 * 4)
        var sum: Long = 0
        var len = 0
        var off = 0
        for (key in properties) {
            conn.setRequestProperty(key.toString(), properties[key.toString()].toString())
        }
        conn.setHostnameVerifier { _, session -> true }
        try {
            fos = FileOutputStream(filePath)
            conn.connect()

            while (conn.inputStream.read(buffer).apply { len = this } > 0) {
                fos.write(buffer, off, len)
            }
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            println("wrong")
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
    }

    fun threadDownload(
        url: String,
        properties: HashMap<String, String>,
        filePath: String,
        tempPath:String,
        chunkSize_kb: Int = 16,
        uiHandler: UIHandler,
        progressBarId: Int = -1
    ): Boolean {
        try {
            //println(":")
            HttpsUtil.trustEveryone()
            var contentLength = -1
            while (contentLength == -1) {
                val conn = URL(url).openConnection() as HttpsURLConnection
                for (key in properties) {
                    conn.setRequestProperty(key.toString(), properties[key.toString()].toString())
                }
                conn.setRequestProperty("Referer", "https://www.pixiv.net")
                conn.setHostnameVerifier { _, session -> true }
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                contentLength = conn.contentLength
            }
            println("length $contentLength")

            if (contentLength != -1) {
                if (progressBarId!=-1) uiHandler.post{
                    uiHandler.updateProgressBar(progressBarId,0)
                }
                val progress = Progress(0, contentLength, System.currentTimeMillis())
                val raf = RandomAccessFile(tempPath, "rws")
                raf.setLength(contentLength.toLong())
                raf.close()
                var segments: Int =
                    Math.ceil(contentLength / 1024 / chunkSize_kb.toDouble()).toInt()
                if(segments>200){
                    segments = 200
                }
                println("sengments: $segments")
                var cursor = 0
                val jobs: MutableList<Thread> = mutableListOf()
//            val percent:Float = 100F/segments
                for (segment in 0 until segments) {
                    if (segment == segments - 1) {
                        //                spos.add(cursor)
                        //                epos.add(contentLength)
                        var start = cursor
                        var end = contentLength
                        val x = Thread {
                            taskDownload(
                                url,
                                start,
                                end,
                                tempPath,
                                properties,
                                progress,
                                uiHandler,
                                progressBarId
                            )
                        }
                        x.isDaemon = true
                        jobs.add(x)
                    } else {
                        //                spos.add(cursor)
                        //                cursor+=chunkSize_kb*1024
                        //                epos.add(cursor-1)
                        var start = cursor
                        cursor += chunkSize_kb * 1024
                        var end = cursor - 1
                        val x = Thread {
                            taskDownload(
                                url,
                                start,
                                end,
                                tempPath,
                                properties,
                                progress,
                                uiHandler,
                                progressBarId
                            )
                        }
                        x.isDaemon = true
                        jobs.add(x)
                    }
                }
                for (job in jobs) {
                    job.start()
                }
                for (job in jobs) {
                    job.join()
                }
                if(progressBarId!=-1) {
                    val msg = Message()
                    msg.what = UIHandler.UPDATE_PROGRESS_BAR
                    val detail =
                        UIDetail(progressBarId, int = 100)
                    msg.obj = detail
                    uiHandler.sendMessage(msg)
                }
                File(tempPath).copyTo(File(filePath))
                File(tempPath).delete()
            }
            //println(";")
            return true
        } catch (e: Exception) {
            println("download timeout")
            return false
        }
    }

    fun taskDownload(
        url: String,
        start: Int,
        end: Int,
        filePath: String,
        properties: HashMap<String, String>,
        progress: Progress,
        uiHandler: UIHandler,
        progressBarId: Int
    ) {
        while (true) {
            var done = 0
            try {
                //println(".")
                val conn = URL(url).openConnection() as HttpsURLConnection
                conn.requestMethod = "GET"
                //设置连接时间
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                //在请求头中封装客户端所需要的数据
                conn.setRequestProperty("range", "bytes=$start-$end")
                for (key in properties) {
                    conn.setRequestProperty(key.toString(), properties[key.toString()].toString())
                }
                conn.setRequestProperty("Referer", "https://www.pixiv.net")
                conn.setHostnameVerifier { _, session -> true }
                val raf = RandomAccessFile(filePath, "rws")
                var inputStream = conn.inputStream
                //创建文件
                //跳到指定位置
                raf.seek(start.toLong())
                var len = -1
                //设定缓冲区
                var buf = ByteArray(1024)
                var flag = true
                while (flag) {
                    //读取数据，返回下标
                    len = inputStream.read(buf)
                    //TODO 可以在此处记录下载文件的坐标，从而实现断点下载
                    flag = len != -1
                    //写数据
                    if (flag) {
                        raf.write(buf, 0, len)
                        progress.done += len
                        //println("len $len done ${progress.done}")
                        done += len
                        if (progressBarId != -1 && ((System.currentTimeMillis() - progress.lastUpdateTime))>41L) {
                            progress.lastUpdateTime = System.currentTimeMillis()
                            val msg = Message()
                            msg.what = UIHandler.UPDATE_PROGRESS_BAR
                            val detail =
                                UIDetail(progressBarId, int = progress.done * 100 / progress.total)
                            msg.obj = detail
                            uiHandler.sendMessage(msg)
                        }
                    }
                }
                raf.close()
                return
            } catch (e: IOException) {
                progress.done -= done
                //(e.message)
                //println(e.stackTrace)
                //println("Task(End at $end): retry")
            }
        }
    }
}