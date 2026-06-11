package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.tilNombre.editText?.text.toString().trim()
            val correo = binding.tilCorreo.editText?.text.toString().trim()
            val contrasena = binding.tilContrasena.editText?.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty()) {
                binding.tilNombre.error = "Ingresa tu nombre"
                return@setOnClickListener
            }
            binding.tilNombre.error = null

            if (correo.isEmpty()) {
                binding.tilCorreo.error = "Ingresa tu correo"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                binding.tilCorreo.error = "Correo no válido"
                return@setOnClickListener
            }
            binding.tilCorreo.error = null

            if (contrasena.isEmpty()) {
                binding.tilContrasena.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }
            if (contrasena.length < 6) {
                binding.tilContrasena.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }
            binding.tilContrasena.error = null

            // Registro con Firebase
            auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cuenta creada", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_register_to_personalInfo)
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