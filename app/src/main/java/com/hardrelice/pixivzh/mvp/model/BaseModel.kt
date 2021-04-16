package com.hardrelice.pixivzh.mvp.model

data class BaseModel<T>(val code:Int, val message:String, val data:T)