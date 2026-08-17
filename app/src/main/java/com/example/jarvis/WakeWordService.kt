package com.example.jarvis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.BuiltInKeyword
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import java.util.Locale

// Get this from console.picovoice.ai (free tier) — do not commit a real key to a public repo.
private const val PICOVOICE_ACCESS_KEY = "YOUR_ACCESS_KEY_HERE"

/**
 * Long-running foreground service.
 *
 * Flow once the wake word fires:
 *   1. Greet the user ("Hi sir")
 *   2. Listen for one spoken command
 *   3. Hand it to IntentRouter
 *   4. Speak the result
 *   5. Go back to listening for the wake word
 */
class WakeWordService : Service() {

    private lateinit var tts: TextToSpeech
    private lateinit var voiceListener: VoiceCommandListener
    private lateinit var porcupineManager: PorcupineManager
    private val router = IntentRouter()

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault()
            }
        }
        voiceListener = VoiceCommandListener(this)
        startForegroundWithNotification()

        porcupineManager = PorcupineManager.Builder()
            .setAccessKey(PICOVOICE_ACCESS_KEY)
            .setKeyword(BuiltInKeyword.JARVIS)
            .build(applicationContext, object : PorcupineManagerCallback {
                override fun invoke(keywordIndex: Int) {
                    onWakeWordDetected()
                }
            })
        porcupineManager.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /** Called when Porcupine detects "Jarvis". */
    fun onWakeWordDetected() {
        porcupineManager.stop() // free up the mic for the speech recognizer
        speak("Hi sir, how can I help?") {
            voiceListener.startListening(
                onResult = { spokenCommand -> handleCommand(spokenCommand) },
                onError = {
                    speak("Sorry sir, I didn't catch that.")
                    porcupineManager.start() // go back to listening for the wake word
                }
            )
        }
    }

    private fun handleCommand(spokenCommand: String) {
        val reply = router.handle(this, spokenCommand)
        speak(reply)
        porcupineManager.start() // resume wake-word listening now that we're done
    }

    private fun speak(text: String, onDone: (() -> Unit)? = null) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        onDone?.invoke() // simplistic; for production, hook UtteranceProgressListener instead
    }

    private fun startForegroundWithNotification() {
        val channelId = "jarvis_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Jarvis Assistant", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Jarvis is listening")
            .setContentText("Say \"Hey Jarvis\" to give a command")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        voiceListener.destroy()
        porcupineManager.stop()
        porcupineManager.delete()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
