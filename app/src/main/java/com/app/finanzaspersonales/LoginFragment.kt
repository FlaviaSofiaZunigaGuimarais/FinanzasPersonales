package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_login_to_home)
            return
        }

        binding.btnIngresar.setOnClickListener {
            val correo = binding.tilCorreo.editText?.text.toString().trim()
            val contrasena = binding.tilContrasena.editText?.text.toString().trim()

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

            auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_login_to_home)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvRegistrarse.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.tvRestablecer.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}