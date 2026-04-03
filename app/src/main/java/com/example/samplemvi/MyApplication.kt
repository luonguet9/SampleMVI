package com.example.samplemvi

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
		
		setupStrictMode()
	}
	
	private fun setupStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(
				StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build()
			)
			
			StrictMode.setVmPolicy(
				StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build()
			)
		}
	}
}


