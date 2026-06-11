package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentDetalleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tipo = arguments?.getString("tipo") ?: "-"
        val monto = arguments?.getDouble("monto") ?: 0.0
        val categoria = arguments?.getString("categoria") ?: "-"
        val fecha = arguments?.getString("fecha") ?: "-"
        val descripcion = arguments?.getString("descripcion") ?: "-"
        val id = arguments?.getString("id") ?: ""

        binding.tvTipoMovimiento.text = tipo
        binding.tvMontoDetalle.text = "$${"%.2f".format(monto)}"
        binding.tvCategoriaDetalle.text = "Categoría: $categoria"
        binding.tvFechaDetalle.text = "Fecha: $fecha"
        binding.tvDescripcionDetalle.text = "Descripción: $descripcion"

        binding.btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar movimiento")
                .setMessage("¿Estás seguro de que quieres eliminar este movimiento?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val uid = auth.currentUser?.uid ?: return@setPositiveButton
                    db.collection("usuarios").document(uid)
                        .collection("movimientos").document(id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Movimiento eliminado", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}