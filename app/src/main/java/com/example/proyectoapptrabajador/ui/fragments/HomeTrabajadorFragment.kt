package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentHomeTrabajadorBinding

class HomeTrabajadorFragment : Fragment() {

    private var _binding: FragmentHomeTrabajadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeTrabajadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Botón Ver Mis Citas
        binding.cardMisCitas.setOnClickListener {
            findNavController().navigate(R.id.action_homeTrabajadorFragment_to_misCitasFragment)
        }

        // Botón Actualizar Foto
        binding.cardActualizarFoto.setOnClickListener {
            findNavController().navigate(R.id.action_homeTrabajadorFragment_to_workerRegisterStep2Fragment)
        }

        // Botón Actualizar Ocupaciones
        binding.cardActualizarOcupaciones.setOnClickListener {
            findNavController().navigate(R.id.action_homeTrabajadorFragment_to_workerRegisterStep3Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
