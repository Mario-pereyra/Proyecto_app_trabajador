package com.example.proyectoapptrabajador.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.data.network.RetrofitHelper
import com.example.proyectoapptrabajador.repositories.AppRepository
import com.example.proyectoapptrabajador.data.datastore.TokenDataStore

class MainActivity : AppCompatActivity() {
    companion object {
        private var _repository: AppRepository? = null
        val repository: AppRepository
            get() {
                if (_repository == null) {
                    throw IllegalStateException("Repository no inicializado")
                }
                return _repository!!
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializamos el repositorio una sola vez con el contexto de la aplicación
        if (_repository == null) {
            val apiClient = RetrofitHelper.getInstance(applicationContext)
            val tokenDataStore = TokenDataStore(applicationContext)
            _repository = AppRepository(apiClient, tokenDataStore)
        }
        setContentView(R.layout.activity_main)
    }
}
