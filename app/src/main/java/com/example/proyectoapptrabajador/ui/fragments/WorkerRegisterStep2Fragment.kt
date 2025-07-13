package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep2Binding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class WorkerRegisterStep2Fragment : Fragment() {

    private var _binding: FragmentWorkerRegisterStep2Binding? = null
    private val binding get() = _binding!!

    private val registerViewModel: WorkerRegisterViewModel by activityViewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivProfile.setImageURI(it)
            registerViewModel.profilePictureUri.value = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerRegisterStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.btnSeleccionarFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSiguiente.setOnClickListener {
            if (registerViewModel.profilePictureUri.value == null) {
                Toast.makeText(context, "Por favor, selecciona una foto de perfil", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log del estado actual después de Pantalla 2
            Log.d("WorkerRegisterStep2", "=== DATOS DESPUÉS DE PANTALLA 2 ===")
            Log.d("WorkerRegisterStep2", "Name: ${registerViewModel.name.value}")
            Log.d("WorkerRegisterStep2", "LastName: ${registerViewModel.lastName.value}")
            Log.d("WorkerRegisterStep2", "Email: ${registerViewModel.email.value}")
            Log.d("WorkerRegisterStep2", "Password: ${registerViewModel.password.value}")
            Log.d("WorkerRegisterStep2", "ProfilePictureUri: ${registerViewModel.profilePictureUri.value}")
            Log.d("WorkerRegisterStep2", "Selected Categories: ${registerViewModel.getSelectedCategoriesDebug()}")
            Log.d("WorkerRegisterStep2", "==========================================")

            // Subir foto de perfil con token
            registerViewModel.uploadProfilePicture(requireActivity().contentResolver)
        }

        // Si el usuario vuelve atrás, mostramos la foto que ya había seleccionado
        registerViewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            uri?.let { binding.ivProfile.setImageURI(it) }
        }
    }

    private fun setupObservers() {
        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSiguiente.isEnabled = !isLoading
            binding.btnSiguiente.text = if (isLoading) "Subiendo foto..." else "Siguiente"
        }

        registerViewModel.photoUploadResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Foto actualizada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_workerRegisterStep2Fragment_to_homeTrabajadorFragment)
            }
        }

        registerViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}