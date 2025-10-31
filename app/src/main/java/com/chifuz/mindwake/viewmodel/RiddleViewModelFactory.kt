package com.chifuz.mindwake.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RiddleViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiddleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RiddleViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}