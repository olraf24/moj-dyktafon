package com.example.voicerecorder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicerecorder.data.Recording
import com.example.voicerecorder.databinding.ActivityMainBinding
import com.example.voicerecorder.utils.AudioPlayer
import com.example.voicerecorder.utils.AudioRecorder
import com.example.voicerecorder.utils.PermissionHelper
import com.example.voicerecorder.utils.RecorderViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RecorderViewModel by viewModels()
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var recordingAdapter: RecordingAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            setupRecording()
        } else {
            Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        permissionHelper = PermissionHelper(this)
        audioRecorder = AudioRecorder()
        audioPlayer = AudioPlayer()
        
        setupRecyclerView()
        observeViewModel()
        
        if (permissionHelper.hasAllPermissions()) {
            setupRecording()
        } else {
            permissionHelper.requestPermissions()
        }
    }

    private fun setupRecyclerView() {
        recordingAdapter = RecordingAdapter(
            onPlayClick = { recording -> playRecording(recording) },
            onDeleteClick = { recording -> showDeleteDialog(recording) }
        )
        
        binding.recyclerViewRecordings.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recordingAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.recordings.observe(this) { recordings ->
            recordingAdapter.submitList(recordings)
            binding.textEmptyState.isVisible = recordings.isEmpty()
        }
        
        viewModel.isRecording.observe(this) { isRecording ->
            updateRecordingUI(isRecording)
        }
    }

    private fun setupRecording() {
        binding.fabRecord.setOnClickListener {
            if (viewModel.isRecording.value == true) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun startRecording() {
        val outputFile = viewModel.createNewRecordingFile()
        val quality = viewModel.getAudioQuality()
        
        if (audioRecorder.startRecording(outputFile, quality)) {
            viewModel.setRecordingState(true)
            Toast.makeText(this, "Rozpoczęto nagrywanie", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Błąd rozpoczynania nagrywania", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        val recordedFile = audioRecorder.stopRecording()
        viewModel.setRecordingState(false)
        
        if (recordedFile != null) {
            viewModel.loadRecordings()
            Toast.makeText(this, getString(R.string.recording_saved), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Błąd zapisywania nagrania", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRecordingUI(isRecording: Boolean) {
        if (isRecording) {
            binding.fabRecord.setImageResource(R.drawable.ic_stop)
            binding.textStatus.text = getString(R.string.stop_recording)
            binding.fabRecord.backgroundTintList = getColorStateList(R.color.red)
        } else {
            binding.fabRecord.setImageResource(R.drawable.ic_mic)
            binding.textStatus.text = getString(R.string.start_recording)
            binding.fabRecord.backgroundTintList = getColorStateList(R.color.purple_500)
        }
    }

    private fun playRecording(recording: Recording) {
        if (audioPlayer.isPlaying()) {
            audioPlayer.stop()
        } else {
            audioPlayer.play(recording.file) {
                // Callback when playback completes
            }
        }
    }

    private fun showDeleteDialog(recording: Recording) {
        AlertDialog.Builder(this)
            .setTitle("Usuń nagranie")
            .setMessage("Czy na pewno chcesz usunąć nagranie \"${recording.name}\"?")
            .setPositiveButton("Usuń") { _, _ ->
                if (viewModel.deleteRecording(recording)) {
                    Toast.makeText(this, "Nagranie usunięte", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Błąd usuwania nagrania", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecorder.release()
        audioPlayer.release()
    }
}