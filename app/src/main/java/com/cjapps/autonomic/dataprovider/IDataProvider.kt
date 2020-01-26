package com.cjapps.autonomic.dataprovider

/**
 * Created by cjgonz on 2020-01-19.
 */
interface IDataProvider {
    fun getString(key: String, defaultValue: String? = null): String?
    fun getLong(key: String, defaultValue: Long): Long
    fun putString(key: String, value: String)
    fun putLong(key: String, value: Long)
}