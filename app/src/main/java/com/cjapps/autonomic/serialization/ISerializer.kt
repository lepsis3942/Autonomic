package com.cjapps.autonomic.serialization

import kotlin.reflect.KClass

/**
 * Created by cjgonz on 2020-03-28.
 */
interface ISerializer {
    fun <T: Any> toJson(objToSerialize: T, clazz: KClass<T>): String
    fun <T: Any> fromJson(objString: String, clazz: KClass<T>): T?
}