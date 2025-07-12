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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentEditProfileBinding
import com.example.proyectoapptrabajador.ui.activities.MainActivity
import com.example.proyectoapptrabajador.ui.adapters.CategoriesSelectionAdapter
import com.example.proyectoapptrabajador.ui.viewmodels.EditProfileViewModel
import com.example.proyectoapptrabajador.ui.viewmodels.factories.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels {
        ViewModelFactory(MainActivity.repository)
    }

    private lateinit var categoriesAdapter: CategoriesSelectionAdapter
    private val currentWorkerId = 1 // TODO: Obtener del token o sesión actual

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
                    .into(binding.imgCurrentPhoto)

                // Subir la imagen inmediatamente
                uploadImage(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupEventListeners()

        // Cargar el perfil actual del trabajador
        viewModel.loadWorkerProfile(currentWorkerId)
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CategoriesSelectionAdapter { categoryId ->
            viewModel.toggleCategory(categoryId)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun setupObservers() {
        viewModel.workerProfile.observe(viewLifecycleOwner) { profile ->
            // Cargar la foto actual del trabajador
            val imageUrl = profile.pictureUrl
            if (!imageUrl.isNullOrEmpty() && imageUrl != "null") {
                Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imgCurrentPhoto)
            } else {
                binding.imgCurrentPhoto.setImageResource(R.mipmap.ic_launcher)
            }
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.updateData(categories)
        }

        viewModel.selectedCategories.observe(viewLifecycleOwner) { selectedIds ->
            categoriesAdapter.updateSelection(selectedIds)
        }

        viewModel.photoUploadStatus.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
            }
        }

        viewModel.categoriesUpdateStatus.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Categorías actualizadas correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Volver a la pantalla anterior
                viewModel.clearStatus()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupEventListeners() {
        binding.btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnSaveChanges.setOnClickListener {
            viewModel.saveAllChanges(currentWorkerId)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "profile_picture_${System.currentTimeMillis()}.jpg")

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
