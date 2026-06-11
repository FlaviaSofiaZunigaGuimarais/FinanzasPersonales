package com.app.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.finanzaspersonales.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val listaMovimientos = mutableListOf<Movimiento>()
    private lateinit var adapter: MovimientoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        adapter = MovimientoAdapter(listaMovimientos) { movimiento ->
            val bundle = Bundle().apply {
                putString("tipo", movimiento.tipo)
                putDouble("monto", movimiento.monto)
                putString("categoria", movimiento.categoria)
                putString("fecha", movimiento.fecha)
                putString("descripcion", movimiento.descripcion)
            }
            findNavController().navigate(R.id.action_home_to_detalle, bundle)
        }
        binding.rvMovimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovimientos.adapter = adapter

        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: "Usuario"
                binding.tvBienvenidoHome.text = "Hola, $nombre"
            }

        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_agregar)
        }

        binding.btnCuenta.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_cuenta)
        }

        cargarMovimientos(uid)
    }

    private fun cargarMovimientos(uid: String) {
        db.collection("usuarios").document(uid)
            .collection("movimientos")
            .get()
            .addOnSuccessListener { result ->
                listaMovimientos.clear()
                var saldoTotal = 0.0
                for (doc in result) {
                    val movimiento = doc.toObject(Movimiento::class.java).copy(id = doc.id)
                    listaMovimientos.add(movimiento)
                    if (movimiento.tipo == "Ingreso") saldoTotal += movimiento.monto
                    else saldoTotal -= movimiento.monto
                }
                adapter.notifyDataSetChanged()
                binding.tvSaldo.text = "Saldo total: $${"%.2f".format(saldoTotal)}"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}