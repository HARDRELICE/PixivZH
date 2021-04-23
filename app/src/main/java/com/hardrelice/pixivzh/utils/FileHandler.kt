package com.hardrelice.pixivzh

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.hardrelice.pixivzh.utils.ApplicationUtil
import java.io.BufferedOutputStream
import java.io.File


object FileHandler {
    lateinit var context: Context

    const val emulated0 = "/storage/emulated/0/"

    fun storage(filePath: String): String {
        return join(emulated0, filePath)
    }

    fun filesDir(): String {
        context = ApplicationUtil.ApplicationContext!!
        return context!!.filesDir.absolutePath
    }

    fun cacheDir(): String {
        context = ApplicationUtil.ApplicationContext!!
        return context!!.cacheDir.absolutePath
    }

    fun externalCacheDir(): String {
        context = ApplicationUtil.ApplicationContext!!
        return context!!.externalCacheDir?.absolutePath!!
    }


    fun join(vararg dirs: String): String {
        var retDir = ""
        for (dir in dirs) {
            retDir += "/$dir"
        }
//        println(retDir)
        return retDir
    }

    fun checkDir(path: String): Boolean {
        var ret = true
        if (!File(path).exists()) {
            File(path).mkdir().also { ret = it }
        }
        return ret
    }

    fun printDirs() {
        println("${filesDir()}\n${cacheDir()}\n${externalCacheDir()}")
    }

    fun getIllustFolder(pid: String, fileName: String = ""): String {
        return join(externalCacheDir(), pid, fileName)
    }

    fun getUserFolder(uid: String, fileName: String = ""): String {
        checkDir(join(externalCacheDir(), "users"))
        checkDir(join(externalCacheDir(), "users", uid))
        return join(externalCacheDir(), "users", uid, fileName)
    }

    fun putImageMedia(imagePath: String, imageName: String, artistName: String) {
        val resolver = ApplicationUtil.ApplicationContext!!.contentResolver
        MediaStore.Images.Media.insertImage(resolver, imagePath, imageName, imageName)
        val imageCollection =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        println(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            put(MediaStore.Images.Media.TITLE, imageName)
            put(MediaStore.Images.Media.DESCRIPTION, imageName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                put(MediaStore.Images.Media.ARTIST, artistName)
            }
        }

        val targetImageFile = resolver.insert(imageCollection, newImageDetails)
        println(targetImageFile.toString())
        if (targetImageFile != null) {
            File(imagePath).inputStream().use { input ->
                BufferedOutputStream(resolver.openOutputStream(targetImageFile)).use { output ->
                    input.copyTo(output)
                }
            }
        }
//        targetImageFile?.let { resolver.openOutputStream(it)
//            imagePath.toUri().toFile().apply {
//                this.copyTo(it.toFile())
//            }
//        }

    }

    fun writeFileToStorage(path: String, mediaName: String, fileName: String) {
        checkDir(storage(mediaName))
        File(path).inputStream().use { input ->
            BufferedOutputStream(
                File(
                    storage(
                        join(
                            mediaName,
                            fileName
                        )
                    )
                ).outputStream()
            ).use { output ->
                input.copyTo(output)
            }
        }

    }

    fun refreshMediaStore(filePath: String) {
//        context.contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            arrayOf(
//                MediaStore.Images.Media.TITLE,
//                MediaStore.Images.Media._ID,
//                MediaStore.Images.Media.DISPLAY_NAME
//            ),
//            null, null, null
//        )
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri: Uri = Uri.fromFile(File(filePath))
        intent.data = uri
        context.sendBroadcast(intent)
    }


}