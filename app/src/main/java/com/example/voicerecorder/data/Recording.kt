package com.example.voicerecorder.data

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class Recording(
    val file: File,
    val name: String = file.name,
    val date: Date = Date(file.lastModified()),
    val duration: Long = 0L,
    val tag: String = "Inne"
) {
    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun getFormattedDuration(): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    fun getSizeInMB(): String {
        val sizeInBytes = file.length()
        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", sizeInMB)
    }
}