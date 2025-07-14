package com.example.proyectoapptrabajador.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentHomeTrabajadorBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import kotlinx.coroutines.launch

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

        // Botón Cerrar Sesión
        binding.cardCerrarSesion.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setIcon(R.drawable.ic_logout)
            .setPositiveButton("Sí, cerrar sesión") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Personalizar colores del diálogo para mantener consistencia UX
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                resources.getColor(R.color.colorError, null)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                resources.getColor(R.color.colorPrimary, null)
            )
        }

        dialog.show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                // Limpiar el token almacenado
                MainActivity.repository.saveToken("")

                // Navegar a la pantalla de login y limpiar el stack de navegación
                findNavController().navigate(R.id.action_homeTrabajadorFragment_to_loginFragment)

                // Opcional: Reiniciar la actividad para asegurar que se limpie todo el estado
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } catch (e: Exception) {
                // En caso de error, al menos navegar al login
                findNavController().navigate(R.id.action_homeTrabajadorFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
