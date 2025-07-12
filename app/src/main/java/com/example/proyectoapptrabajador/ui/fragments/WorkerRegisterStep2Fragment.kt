package com.example.proyectoapptrabajador.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentWorkerRegisterStep2Binding
import com.example.proyectoapptrabajador.ui.viewmodels.WorkerRegisterViewModel

class WorkerRegisterStep2Fragment : Fragment() {
    private var _binding: FragmentWorkerRegisterStep2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkerRegisterViewModel by activityViewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                binding.imgProfilePreview.setImageURI(it)
                viewModel.profilePictureUri.value = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkerRegisterStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.btnNextStep.setOnClickListener {
            findNavController().navigate(R.id.action_workerRegisterStep2Fragment_to_workerRegisterStep3Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
