package com.example.voicerecorder.utils

import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    
    fun startRecording(file: File, bitrate: Int = 128000): Boolean {
        return try {
            outputFile = file
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(android.content.Context())
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(bitrate)
                setAudioSamplingRate(48000)
                setOutputFile(file.absolutePath)
                
                prepare()
                start()
            }
            
            isRecording = true
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            outputFile
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    fun isRecording(): Boolean = isRecording
    
    fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
    }
}