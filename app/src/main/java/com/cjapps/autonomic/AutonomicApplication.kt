package com.cjapps.autonomic

import android.app.Application
import android.util.Log
import com.cjapps.autonomic.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import fr.bipi.tressence.common.formatter.Formatter
import fr.bipi.tressence.file.FileLoggerTree
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-09-14.
 */
class AutonomicApplication: Application(), HasAndroidInjector {

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            val fileLoggerTree: Timber.Tree = FileLoggerTree.Builder()
                .withFileName("file%g.log")
                .withDirName(filesDir.absolutePath)
                .withFormatter(object : Formatter {
                    override fun format(priority: Int, tag: String?, message: String): String {
                        val df: DateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
                        val date: String = df.format(Calendar.getInstance().time)
                        return "[$date] $priority - $tag - $message \n"
                    }
                })
                .withSizeLimit(200000)
                .withFileLimit(3)
                .withMinPriority(Log.DEBUG)
                .appendToFile(true)
                .build()
            Timber.plant(fileLoggerTree)
        }

        DaggerAppComponent.builder().applicationContext(applicationContext).build().inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}