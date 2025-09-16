package com.servicebio.compose.application

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class MainApplication : Application() {

    companion object {
        lateinit var context: WeakReference<Context>
            private set

        fun init(application: Application) {
            context = WeakReference(application.applicationContext)
        }
    }

    override fun onCreate() {
        super.onCreate()

        init(this)
    }

}
