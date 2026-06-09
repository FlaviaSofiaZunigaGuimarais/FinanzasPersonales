package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.finanzaspersonales.databinding.FragmentDetalleBinding

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tipo = arguments?.getString("tipo") ?: "-"
        val monto = arguments?.getDouble("monto") ?: 0.0
        val categoria = arguments?.getString("categoria") ?: "-"
        val fecha = arguments?.getString("fecha") ?: "-"
        val descripcion = arguments?.getString("descripcion") ?: "-"

        binding.tvTipoMovimiento.text = tipo
        binding.tvMontoDetalle.text = "$${"%.2f".format(monto)}"
        binding.tvCategoriaDetalle.text = "Categoría: $categoria"
        binding.tvFechaDetalle.text = "Fecha: $fecha"
        binding.tvDescripcionDetalle.text = "Descripción: $descripcion"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}