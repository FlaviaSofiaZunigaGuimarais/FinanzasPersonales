package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CuentaFragment : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid ?: return

        // Cargar datos del usuario desde Firestore
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: "-"
                val apellidos = doc.getString("apellidos") ?: "-"
                val correo = doc.getString("correo") ?: "-"
                val telefono = doc.getString("telefono") ?: "-"
                val fecha = doc.getString("fechaNacimiento") ?: "-"

                binding.tvNombreCuenta.text = "$nombre $apellidos"
                binding.tvCorreoCuenta.text = correo
                binding.tvTelefonoCuenta.text = "Teléfono: $telefono"
                binding.tvFechaCuenta.text = "Fecha de nacimiento: $fecha"
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }

        // Cerrar sesión
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}