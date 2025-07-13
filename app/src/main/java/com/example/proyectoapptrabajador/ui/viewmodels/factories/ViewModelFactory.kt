package com.example.proyectoapptrabajador.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectoapptrabajador.repositories.AppRepository
import com.example.proyectoapptrabajador.ui.viewmodels.LoginViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.CitasViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.ChatViewModel

class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(WorkerRegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkerRegisterViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CitasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CitasViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}