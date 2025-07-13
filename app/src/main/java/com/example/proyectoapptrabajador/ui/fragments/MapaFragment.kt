package com.example.proyectoapptrabajador.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectoapptrabajador.R
import com.example.proyectoapptrabajador.databinding.FragmentMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!

    private val args: MapaFragmentArgs by navArgs()
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar botones
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnZoomIn.setOnClickListener {
            if (::mMap.isInitialized) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn())
            }
        }

        binding.btnZoomOut.setOnClickListener {
            if (::mMap.isInitialized) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut())
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val latitude = args.latitude.toDouble()
            val longitude = args.longitude.toDouble()
            val location = LatLng(latitude, longitude)

            // Agregar marcador en la ubicación
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Ubicación de la cita")
            )

            // Mover la cámara a la ubicación con zoom apropiado
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            // Configurar controles del mapa
            mMap.uiSettings.apply {
                isZoomControlsEnabled = false // Usamos nuestros botones personalizados
                isMapToolbarEnabled = false
                isCompassEnabled = true
                isMyLocationButtonEnabled = false
            }

        } catch (e: NumberFormatException) {
            // Si hay error en las coordenadas, mostrar ubicación por defecto
            val defaultLocation = LatLng(-17.783327, -63.182140) // Santa Cruz, Bolivia
            mMap.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Ubicación no disponible")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
