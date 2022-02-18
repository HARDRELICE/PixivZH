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
        getUrl: String,
        headers: Map<String, String>,
        params: HashMap<String, Any?> = hashMapOf()
    ): Document? {
        var res: Document? = null
        var url = getUrl
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
        jsonUrl: String,
        headers: Map<String, String>,
        params: HashMap<String, Any?> = hashMapOf()
    ): String? {
        var url = jsonUrl
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

    fun call(url: String, properties: Map<String, String>): String {
        val conn = URL(url).openConnection() as HttpsURLConnection
        for (pair in properties) {
            conn.setRequestProperty(pair.key, pair.value)
        }
        conn.setHostnameVerifier { _, _ -> true }
        val content = conn.content
        println(content.toString())
        return content.toString()
    }

    fun download(url: String, properties: Map<String, String>, filePath: String) {
        while (true) {
            val conn = URL(url).openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            for (pair in properties) {
                conn.setRequestProperty(pair.key, pair.value)
            }
            conn.setHostnameVerifier { _, _ -> true }
            try {
                conn.inputStream.use {
                    inputStream -> BufferedOutputStream(FileOutputStream(filePath)).use {
                        bufferedOutputStream -> inputStream.copyTo(bufferedOutputStream)
                    }
                }
                return
            } catch (e: Exception) {
                Log.e("download", e.message!!)
            } finally {
                conn.disconnect()
            }
        }
    }

    fun mdownload(url: String, properties: HashMap<String, String>, filePath: String) {
        val conn = URL(url).openConnection() as HttpsURLConnection
        var fos: FileOutputStream? = null
        val buffer = ByteArray(1024 * 4)
        var sum: Long = 0
        var len = 0
        val off = 0
        for (pair in properties) {
            conn.setRequestProperty(pair.key, pair.value)
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
            conn.disconnect()
        }
    }

    fun threadDownload(
        url: String,
        properties: Map<String, String>,
        filePath: String,
        tempPath:String,
        chunkSize_kb: Int = 16,
        uiHandler: UIHandler,
        progressBarId: Int = -1
    ): Boolean {
        try {
            var contentLength = -1
            while (contentLength == -1) {
                val conn = URL(url).openConnection() as HttpsURLConnection
                for (pair in properties) {
                    conn.setRequestProperty(pair.key, pair.value)
                }
                conn.setHostnameVerifier { _, _ -> true }
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
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
                println("sengments: $segments")
                var cursor = 0
                val jobs: MutableList<Thread> = mutableListOf()
                for (segment in 0 until segments) {
                    if (segment == segments - 1) {
                        val start = cursor
                        val end = contentLength
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
                        jobs.add(x)
                    } else {
                        val start = cursor
                        cursor += chunkSize_kb * 1024
                        val end = cursor - 1
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
                    val detail = UIDetail(progressBarId, int = 100)
                    msg.obj = detail
                    uiHandler.sendMessage(msg)
                }
                File(tempPath).copyTo(File(filePath))
                File(tempPath).delete()
            }
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
        properties: Map<String, String>,
        progress: Progress,
        uiHandler: UIHandler,
        progressBarId: Int
    ) {
        while (true) {
            var done = 0
            try {
                val conn = URL(url).openConnection() as HttpsURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.setRequestProperty("range", "bytes=$start-$end")
                for (pair in properties) {
                    conn.setRequestProperty(pair.key, pair.value)
                }
                conn.setHostnameVerifier { _, _ -> true }
                val raf = RandomAccessFile(filePath, "rws")
                val inputStream = conn.inputStream
                raf.seek(start.toLong())
                var len: Int
                val buf = ByteArray(1024)
                var flag = true
                while (flag) {
                    len = inputStream.read(buf)
                    //TODO 可以在此处记录下载文件的坐标，从而实现断点下载
                    flag = len != -1
                    if (flag) {
                        raf.write(buf, 0, len)
                        progress.done += len
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
                //println("Task(End at $end): retry")
            }
        }
    }
}