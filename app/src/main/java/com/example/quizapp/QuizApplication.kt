package com.example.quizapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
// import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory // Descomente para RELEASE
import android.util.Log // Importação para logs

@HiltAndroidApp
class QuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("QuizApplication", "onCreate() started.")


        try {
            FirebaseApp.initializeApp(this)
            Log.d("QuizApplication", "FirebaseApp initialized.")
        } catch (e: Exception) {
            Log.e("QuizApplication", "Failed to initialize FirebaseApp: ${e.message}", e)

            return
        }

        // 2. Inicialize o Firebase App Check
        try {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()


            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            Log.d("QuizApplication", "Firebase App Check Debug Provider installed.")



        } catch (e: Exception) {
            Log.e("QuizApplication", "Failed to install App Check provider: ${e.message}", e)

        }

        Log.d("QuizApplication", "onCreate() finished.")
    }
}