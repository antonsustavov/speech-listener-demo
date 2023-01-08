package com.sustav.mytest.view.speech_listener_demo

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sustav.mytest.view.speech_listener_demo.databinding.ActivityMainBinding
import java.util.*

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private val RecordAudioRequestCode = 1
    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }

        Log.d("SPEECH", "Is available: ${SpeechRecognizer.isRecognitionAvailable(this)}")

        //First approach E/SpeechRecognizer: no selected voice recognition service
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        //Second approach E/SpeechRecognizer: bind to recognition service failed
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this,
            ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));

        val voice = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        setUpSpeechListener()

        binding.mybutton.setOnClickListener {
            speechRecognizer!!.startListening(voice)
        }
    }

    private fun setUpSpeechListener() {
        speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                binding.text.text = "Listening..."
                binding.text.hint = "Listening..."
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                binding.mybutton.setImageResource(R.drawable.ic_mic_black_off)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                binding.text.text = data!![0]
                binding.text.hint = "Listening..."
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        speechRecognizer!!.destroy()
        super.onDestroy()
    }
}


