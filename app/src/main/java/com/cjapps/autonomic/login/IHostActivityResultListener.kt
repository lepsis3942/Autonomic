package com.cjapps.autonomic.login

import android.content.Intent

/**
 * Created by cjgonz on 2020-01-05.
 */
interface IHostActivityResultListener {
    fun onHostActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}