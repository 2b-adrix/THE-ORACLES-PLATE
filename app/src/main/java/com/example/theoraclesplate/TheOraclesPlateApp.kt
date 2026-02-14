package com.example.theoraclesplate

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheOraclesPlateApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(this)
    }
}
