package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
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

        binding.btnSeleccionarFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSiguiente.setOnClickListener {
            if (registerViewModel.profilePictureUri.value == null) {
                Toast.makeText(context, "Por favor, selecciona una foto de perfil", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_workerRegisterStep2Fragment_to_workerRegisterStep3Fragment)
        }

        // Si el usuario vuelve atrás, mostramos la foto que ya había seleccionado
        registerViewModel.profilePictureUri.observe(viewLifecycleOwner) { uri ->
            uri?.let { binding.ivProfile.setImageURI(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}