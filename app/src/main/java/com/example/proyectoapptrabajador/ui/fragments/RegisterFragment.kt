package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.databinding.FragmentRegisterBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.RegisterViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupEventListeners()
    }

    private fun setupObservers() {
        viewModel.registrationStatus.observe(viewLifecycleOwner) { isRegistered ->
            if (isRegistered) {
                Toast.makeText(requireContext(), "Registro exitoso. Por favor, inicie sesión.", Toast.LENGTH_LONG).show()
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupEventListeners() {
        binding.btnRegistro.setOnClickListener {
            val nombre = binding.txtNombre.text.toString()
            val apellido = binding.txtApellido.text.toString()
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(nombre, apellido, email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lblLogin.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
