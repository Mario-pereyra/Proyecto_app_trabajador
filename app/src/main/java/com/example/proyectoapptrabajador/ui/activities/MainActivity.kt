package com.example.proyectoapptrabajador.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.data.network.RetrofitHelper
import com.example.proyectoapptrabajador.repositories.AppRepository
import com.example.proyectoapptrabajador.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

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

        // Verificar si el usuario ya está autenticado
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "=== VERIFICANDO ESTADO DE AUTENTICACIÓN ===")

                // Obtener el token del DataStore
                val tokenDataStore = TokenDataStore(applicationContext)
                val savedToken = tokenDataStore.getToken.first()

                if (!savedToken.isNullOrBlank()) {
                    Log.d("MainActivity", "Token encontrado, verificando validez...")

                    // Verificar si el token es válido haciendo una llamada a /me
                    val meResponse = repository.getMe()

                    if (meResponse.isSuccessful) {
                        Log.d("MainActivity", "Token válido, navegando a MisCitasFragment")

                        // Token válido, navegar directamente a la pantalla principal
                        runOnUiThread {
                            try {
                                val navController = findNavController(R.id.nav_host_fragment)
                                navController.navigate(R.id.misCitasFragment)
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error al navegar a MisCitasFragment", e)
                            }
                        }

                    } else {
                        Log.w("MainActivity", "Token inválido, limpiando y permaneciendo en login")

                        // Token inválido, limpiar y quedarse en login
                        repository.saveToken("")
                    }
                } else {
                    Log.d("MainActivity", "No hay token guardado, permaneciendo en LoginFragment")
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Error al verificar autenticación", e)
                // En caso de error, permanecer en login
            }
        }
    }
}
