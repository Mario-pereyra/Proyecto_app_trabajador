package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.databinding.FragmentLoginBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.LoginViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Inyectamos el ViewModel usando nuestra Factory
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Primero, verificamos si ya hay una sesión activa
        viewModel.checkSession()

        setupObservers()
        setupEventListeners()
    }

    private fun setupObservers() {
        // Observador para la sesión automática
        viewModel.sessionStatus.observe(viewLifecycleOwner) { hasSession ->
            if (hasSession) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCategoriesFragment())
            }
        }

        // Observador para el resultado del login manual
        viewModel.loginStatus.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCategoriesFragment())
            }
        }

        // Observador para los mensajes de error
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupEventListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese email y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lblRegistro.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}