package com.hardrelice.pixivzh.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class Property(private val propertyFile: String, val context: Context) {
    lateinit var propertyPreferences: SharedPreferences

    private val defaultProperty = mapOf(
        "exist" to true
    )

    private val property = hashMapOf<String, Any>()

    fun init() {
        propertyPreferences = context.getSharedPreferences(propertyFile, Activity.MODE_PRIVATE)
        if (!propertyPreferences.getBoolean("exist", false)) {
            applyDefaultSettings()
        }
        for (key in defaultProperty.keys){
            if (key!="exist"){
                property[key] = propertyPreferences.getInt(key,0)
            }
        }
        save()
    }

    fun edit(key: String, value: Any) {
        try {
            val editor = propertyPreferences.edit()
            when (value.javaClass.canonicalName) {
                java.lang.String::class.java.canonicalName -> {
                    editor.putString(key, value as String)
                }
                java.lang.Boolean::class.java.canonicalName -> {
                    editor.putBoolean(key, value as Boolean)
                }
                java.lang.Float::class.java.canonicalName -> {
                    editor.putFloat(key, value as Float)
                }
                java.lang.Integer::class.java.canonicalName -> {
                    editor.putInt(key, value as Int)
                }
                java.lang.Long::class.java.canonicalName -> {
                    editor.putLong(key, value as Long)
                }
                "java.util.Collections.SingletonSet" -> {
                    editor.putStringSet(key, value as Set<String>)
                }
                else -> {
                    editor.apply()
                    editor.commit()
                    return
                }
            }
            property[key] = value
            editor.apply()
            editor.commit()
        } catch (e: Exception) {
            e.message?.let { Log.e("property Edit", it) }
        }
    }

    fun edit(map: Map<String, Any>) {
        for (key in map.keys) {
            map[key]?.let { edit(key, it) }
        }
    }

    fun save(){
        edit(property)
    }

    fun getProperty(propertyName: String):Any?{
        return property[propertyName]
    }

    private fun applyDefaultSettings() {
        edit(defaultProperty)
    }
}