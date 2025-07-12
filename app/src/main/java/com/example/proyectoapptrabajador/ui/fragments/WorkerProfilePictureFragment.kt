package com.example.proyectoapptrabajador.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerProfilePictureBinding
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel

class WorkerProfilePictureFragment : Fragment() {
    private var _binding: FragmentWorkerProfilePictureBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerRegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerProfilePictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        binding.btnNext.setOnClickListener {
            // Guardar el URI en el ViewModel ya está hecho en onActivityResult
            // Navegar al siguiente fragmento (ocupaciones)
            val navController = androidx.navigation.fragment.findNavController(this)
            navController.navigate(R.id.action_workerProfilePictureFragment_to_workerCategoryFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.imgProfile.setImageURI(imageUri)
                viewModel.saveProfileImage(imageUri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
