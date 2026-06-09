package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnEnviar.setOnClickListener {
            val correo = binding.tilCorreo.editText?.text.toString().trim()

            if (correo.isEmpty()) {
                binding.tilCorreo.error = "Ingresa tu correo"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                binding.tilCorreo.error = "Correo no válido"
                return@setOnClickListener
            }
            binding.tilCorreo.error = null

            auth.sendPasswordResetEmail(correo)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Correo enviado, revisa tu bandeja",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_forgot_to_login)
                }
        }
    }
}
