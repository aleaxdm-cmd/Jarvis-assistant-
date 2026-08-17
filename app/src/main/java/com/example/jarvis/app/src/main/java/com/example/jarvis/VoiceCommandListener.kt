package com.example.jarvis

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * One-shot listener: call startListening(), get back the recognized sentence via onResult.
 * Call this right after Jarvis greets the user, so it captures the actual command.
 */
class VoiceCommandListener(private val context: Context) {

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun startListening(onResult: (String) -> Unit, onError: (() -> Unit)? = null) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: android.os.Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                onResult(text)
            }

            override fun onError(error: Int) {
                onError?.invoke()
            }

            // Unused callbacks, required by the interface
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    fun destroy() {
        recognizer.destroy()
    }
}
