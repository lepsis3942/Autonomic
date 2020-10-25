package com.cjapps.autonomic

import android.content.Context
import androidx.annotation.StringRes
import dagger.Reusable
import javax.inject.Inject

interface IResourceProvider {
    fun getString(@StringRes id: Int): String
}

@Reusable class ResourceProvider @Inject constructor(private val applicationContext: Context) : IResourceProvider {

    override fun getString(@StringRes id: Int): String {
        return applicationContext.getString(id)
    }
}