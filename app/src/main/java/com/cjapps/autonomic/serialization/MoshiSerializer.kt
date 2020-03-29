package com.cjapps.autonomic.serialization

import com.squareup.moshi.Moshi
import dagger.Reusable
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by cjgonz on 2020-03-28.
 */
@Reusable class MoshiSerializer @Inject constructor(): ISerializer {
    private val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }

    override fun <T: Any> toJson(objToSerialize: T, clazz: KClass<T>): String {
        return moshi.adapter(clazz.java).toJson(objToSerialize)
    }

    override fun <T: Any> fromJson(objString: String, clazz: KClass<T>): T? {
        return moshi.adapter(clazz.java).fromJson(objString)
    }
}