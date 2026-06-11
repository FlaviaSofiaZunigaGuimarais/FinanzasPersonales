package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentAgregarMovimientoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarMovimientoFragment : Fragment() {

    private var _binding: FragmentAgregarMovimientoBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarMovimientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Dropdown de tipo
        val tipos = listOf("Ingreso", "Gasto")
        val tipoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos)
        binding.acTipo.setAdapter(tipoAdapter)

        // Dropdown de categoría
        val categorias = listOf("Comida", "Transporte", "Entretenimiento", "Salud", "Educación", "Ropa", "Servicios", "Otros")
        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias)
        binding.acCategoria.setAdapter(categoriaAdapter)

        binding.btnGuardar.setOnClickListener {
            val tipo = binding.acTipo.text.toString().trim()
            val montoStr = binding.tilMonto.editText?.text.toString().trim()
            val categoria = binding.acCategoria.text.toString().trim()
            val fecha = binding.tilFecha.editText?.text.toString().trim()
            val descripcion = binding.tilDescripcion.editText?.text.toString().trim()

            // Validaciones
            if (tipo.isEmpty()) {
                binding.tilTipo.error = "Selecciona el tipo"
                return@setOnClickListener
            }
            binding.tilTipo.error = null

            if (montoStr.isEmpty()) {
                binding.tilMonto.error = "Ingresa el monto"
                return@setOnClickListener
            }
            binding.tilMonto.error = null

            if (categoria.isEmpty()) {
                binding.tilCategoria.error = "Selecciona una categoría"
                return@setOnClickListener
            }
            binding.tilCategoria.error = null

            if (fecha.isEmpty()) {
                binding.tilFecha.error = "Ingresa la fecha"
                return@setOnClickListener
            }
            binding.tilFecha.error = null

            val monto = montoStr.toDoubleOrNull() ?: 0.0
            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            val movimiento = hashMapOf(
                "tipo" to tipo,
                "monto" to monto,
                "categoria" to categoria,
                "fecha" to fecha,
                "descripcion" to descripcion
            )

            db.collection("usuarios").document(uid)
                .collection("movimientos")
                .add(movimiento)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Movimiento guardado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}