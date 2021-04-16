package com.hardrelice.pixivzh

import org.json.JSONException
import org.json.JSONObject

object JSONUtils {
    fun json2map(jsonString: String?): HashMap<String, Any>? {
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(jsonString)
            val keyIter: Iterator<String> = jsonObject.keys()
            var key: String
            var value: Any
            var valueMap = HashMap<String, Any>()
            while (keyIter.hasNext()) {
                key = keyIter.next()
                value = jsonObject[key] as Any
                valueMap[key] = value
            }
            return valueMap
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}