package com.example.voicerecorder.utils

import android.media.MediaPlayer
import java.io.File

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null
    
    fun play(file: File, onCompletion: (() -> Unit)? = null): Boolean {
        return try {
            stop()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener { 
                    onCompletion?.invoke()
                    stop()
                }
            }
            
            currentFile = file
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentFile = null
    }
    
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
    
    fun release() {
        stop()
    }
}