package com.example.proyectoapptrabajador.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentProfilePictureBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.viewmodels.ProfilePictureViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfilePictureFragment : Fragment() {

    private var _binding: FragmentProfilePictureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfilePictureViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setSelectedImageUri(uri.toString())
                // Mostrar la imagen seleccionada
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(binding.imgPreview)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupEventListeners()
    }

    private fun setupObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "¡Registro completado!", Toast.LENGTH_SHORT).show()
                // Navegar a la pantalla principal del trabajador (Mis Citas)
                // TODO: Implementar navegación a AppointmentsFragment con clearStack
                val action = ProfilePictureFragmentDirections.actionProfilePictureFragmentToAppointmentsFragment()
                findNavController().navigate(action)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupEventListeners() {
        binding.btnSelectPhoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnComplete.setOnClickListener {
            val imageUri = viewModel.selectedImageUri.value
            if (imageUri != null) {
                uploadImage(Uri.parse(imageUri))
            } else {
                Toast.makeText(requireContext(), "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtSkip.setOnClickListener {
            viewModel.skipProfilePicture()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "profile_picture.jpg")

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

            viewModel.uploadProfilePicture(body)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
