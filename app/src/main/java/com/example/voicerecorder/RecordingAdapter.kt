package com.example.voicerecorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecorder.data.Recording
import com.example.voicerecorder.databinding.ItemRecordingBinding

class RecordingAdapter(
    private val onPlayClick: (Recording) -> Unit,
    private val onDeleteClick: (Recording) -> Unit
) : ListAdapter<Recording, RecordingAdapter.RecordingViewHolder>(RecordingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val binding = ItemRecordingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecordingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordingViewHolder(
        private val binding: ItemRecordingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(recording: Recording) {
            binding.apply {
                textFileName.text = recording.name
                textDate.text = recording.getFormattedDate()
                textSize.text = recording.getSizeInMB()
                textTag.text = recording.tag
                
                buttonPlay.setOnClickListener { onPlayClick(recording) }
                buttonDelete.setOnClickListener { onDeleteClick(recording) }
            }
        }
    }
}

class RecordingDiffCallback : DiffUtil.ItemCallback<Recording>() {
    override fun areItemsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem.file.absolutePath == newItem.file.absolutePath
    }

    override fun areContentsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem == newItem
    }
}