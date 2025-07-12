package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.proyectoapptrabajador.databinding.DialogReviewBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.ReviewViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory

class ReviewDialogFragment : DialogFragment() {

    private var _binding: DialogReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReviewViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    // Se necesita el ID de la cita para enviar la calificación
    private var appointmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appointmentId = it.getInt("APPOINTMENT_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
        setupObservers()
    }

    private fun setupEventListeners() {
        binding.btnEnviarReview.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.editTxtReviewComment.text.toString()
            if (rating > 0 && comment.isNotEmpty()) {
                viewModel.postReview(appointmentId, rating, comment)
            } else {
                Toast.makeText(context, "Por favor, añada una calificación y un comentario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNuncaVino.setOnClickListener {
            viewModel.postReview(appointmentId, 1, "El trabajador nunca vino o no realizó el trabajo.")
        }
    }

    private fun setupObservers() {
        viewModel.reviewStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Calificación enviada. ¡Gracias!", Toast.LENGTH_LONG).show()
                dismiss() // Cierra el diálogo
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(appointmentId: Int): ReviewDialogFragment {
            val args = Bundle()
            args.putInt("APPOINTMENT_ID", appointmentId)
            val fragment = ReviewDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
