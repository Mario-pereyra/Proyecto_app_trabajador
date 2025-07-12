package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Configurar opciones de zoom y movimiento
        setupMapOptions()

        // Mostrar la ubicación recibida como parámetro
        val location = LatLng(args.latitude.toDouble(), args.longitude.toDouble())
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        // Configurar el botón de volver
        setupBackButton()
    }

    private fun setupMapOptions() {
        // Habilitar controles de zoom (botones + y -)
        map.uiSettings.isZoomControlsEnabled = true

        // Habilitar todos los gestos para que el usuario pueda mover el mapa libremente
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true
        map.uiSettings.isTiltGesturesEnabled = true

        // Configurar niveles de zoom
        map.setMinZoomPreference(8f)
        map.setMaxZoomPreference(20f)
    }

    private fun setupBackButton() {
        binding.btnConfirmLocation.setOnClickListener {
            // En la app del trabajador, simplemente volver atrás
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
