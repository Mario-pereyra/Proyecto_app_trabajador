package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep1Binding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class WorkerRegisterStep1Fragment : Fragment() {

    private var _binding: FragmentWorkerRegisterStep1Binding? = null
    private val binding get() = _binding!!

    private val registerViewModel: WorkerRegisterViewModel by activityViewModels {
        ViewModelFactory(MainActivity.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerRegisterStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSiguiente.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardamos los datos en el ViewModel compartido
            registerViewModel.name.value = name
            registerViewModel.lastName.value = lastName
            registerViewModel.email.value = email
            registerViewModel.password.value = password

            findNavController().navigate(R.id.action_workerRegisterStep1Fragment_to_workerRegisterStep2Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}