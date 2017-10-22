package org.sharethemeal.hackathon.android

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class InsightsApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this);
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

}