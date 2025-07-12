package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep1Binding
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel

class WorkerRegisterStep1Fragment : Fragment() {
    private var _binding: FragmentWorkerRegisterStep1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerRegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkerRegisterStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistro.setOnClickListener {
            viewModel.name.value = binding.txtNombre.text.toString()
            viewModel.lastName.value = binding.txtApellido.text.toString()
            viewModel.email.value = binding.txtEmail.text.toString()
            viewModel.password.value = binding.txtPassword.text.toString()
            findNavController().navigate(R.id.action_workerRegisterStep1Fragment_to_workerRegisterStep2Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
