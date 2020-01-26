package com.cjapps.autonomic.dataprovider

import android.content.Context
import androidx.core.content.edit
import dagger.Reusable
import javax.inject.Inject

/**
 * DataProvider backed by SharedPreferences
 * Created by cjgonz on 2020-01-19.
 */
@Reusable class LocalDataProvider @Inject constructor(private val applicationContext: Context): IDataProvider {

    private val sharedPreferences by lazy {
        applicationContext.getSharedPreferences("LocalDataProvider", Context.MODE_PRIVATE)
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit {
            putLong(key, value)
        }
    }
}