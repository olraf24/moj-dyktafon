package com.example.voicerecorder.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicerecorder.data.Recording
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecorderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val context = getApplication<Application>()
    private val recordingsDir = File(context.getExternalFilesDir(null), "recordings").apply {
        if (!exists()) mkdirs()
    }
    
    private val _recordings = MutableLiveData<List<Recording>>()
    val recordings: LiveData<List<Recording>> = _recordings
    
    private val _isRecording = MutableLiveData(false)
    val isRecording: LiveData<Boolean> = _isRecording
    
    init {
        loadRecordings()
    }
    
    fun loadRecordings() {
        val files = recordingsDir.listFiles()?.filter { it.isFile && it.extension == "m4a" } ?: emptyList()
        val recordingsList = files.map { file ->
            Recording(
                file = file,
                name = file.nameWithoutExtension,
                date = Date(file.lastModified()),
                tag = guessTagFromName(file.name)
            )
        }.sortedByDescending { it.date }
        
        _recordings.value = recordingsList
    }
    
    fun createNewRecordingFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "REC_$timestamp.m4a"
        return File(recordingsDir, fileName)
    }
    
    fun setRecordingState(isRecording: Boolean) {
        _isRecording.value = isRecording
    }
    
    fun deleteRecording(recording: Recording): Boolean {
        return if (recording.file.delete()) {
            loadRecordings()
            true
        } else {
            false
        }
    }
    
    fun getAudioQuality(): Int {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return when (prefs.getString("quality", "medium")) {
            "low" -> 64000
            "high" -> 256000
            else -> 128000 // medium
        }
    }
    
    private fun guessTagFromName(fileName: String): String {
        val name = fileName.lowercase()
        return when {
            name.contains("spotkanie") || name.contains("meeting") -> "Spotkanie"
            name.contains("notatka") || name.contains("note") -> "Notatka"
            name.contains("pomysl") || name.contains("idea") -> "Pomysł"
            name.contains("wywiad") || name.contains("interview") -> "Wywiad"
            else -> "Inne"
        }
    }
}