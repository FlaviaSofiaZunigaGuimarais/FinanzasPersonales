package com.app.finanzaspersonales

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.finanzaspersonales.databinding.FragmentPersonalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // DatePicker para fecha de nacimiento
        binding.tilFecha.editText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, y, m, d ->
                val fecha = "%02d/%02d/%04d".format(d, m + 1, y)
                binding.tilFecha.editText?.setText(fecha)
            }, year, month, day).show()
        }

        binding.btnContinuar.setOnClickListener {
            val nombre = binding.tilNombre.editText?.text.toString().trim()
            val apellidos = binding.tilApellidos.editText?.text.toString().trim()
            val usuario = binding.tilUsuario.editText?.text.toString().trim()
            val telefono = binding.tilTelefono.editText?.text.toString().trim()
            val fecha = binding.tilFecha.editText?.text.toString().trim()

            if (nombre.isEmpty()) {
                binding.tilNombre.error = "Ingresa tu nombre"
                return@setOnClickListener
            }
            binding.tilNombre.error = null

            if (apellidos.isEmpty()) {
                binding.tilApellidos.error = "Ingresa tus apellidos"
                return@setOnClickListener
            }
            binding.tilApellidos.error = null

            if (usuario.isEmpty()) {
                binding.tilUsuario.error = "Ingresa un nombre de usuario"
                return@setOnClickListener
            }
            binding.tilUsuario.error = null

            if (telefono.isEmpty() || telefono.length < 10) {
                binding.tilTelefono.error = "Ingresa un teléfono válido"
                return@setOnClickListener
            }
            binding.tilTelefono.error = null

            if (fecha.isEmpty()) {
                binding.tilFecha.error = "Ingresa tu fecha de nacimiento"
                return@setOnClickListener
            }
            binding.tilFecha.error = null

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val usuarioData = hashMapOf(
                "nombre" to nombre,
                "apellidos" to apellidos,
                "usuario" to usuario,
                "telefono" to telefono,
                "fechaNacimiento" to fecha,
                "correo" to (auth.currentUser?.email ?: "")
            )

            db.collection("usuarios").document(uid)
                .set(usuarioData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Perfil guardado", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_personalInfo_to_home)
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