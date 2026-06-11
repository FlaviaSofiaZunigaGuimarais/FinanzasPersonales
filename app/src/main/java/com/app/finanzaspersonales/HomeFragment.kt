package com.app.finanzaspersonales

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.finanzaspersonales.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
                var totalIngresos = 0.0
                var totalGastos = 0.0

                for (doc in result) {
                    val movimiento = doc.toObject(Movimiento::class.java).copy(id = doc.id)
                    listaMovimientos.add(movimiento)
                    if (movimiento.tipo == "Ingreso") totalIngresos += movimiento.monto
                    else totalGastos += movimiento.monto
                }

                val saldoTotal = totalIngresos - totalGastos
                adapter.notifyDataSetChanged()
                if (listaMovimientos.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvMovimientos.visibility = View.GONE
                    binding.pieChart.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvMovimientos.visibility = View.VISIBLE
                }
                binding.tvSaldo.text = "Saldo total: $${"%.2f".format(saldoTotal)}"

                actualizarGrafica(totalIngresos, totalGastos)
            }
    }

    private fun actualizarGrafica(ingresos: Double, gastos: Double) {
        if (ingresos == 0.0 && gastos == 0.0) {
            binding.pieChart.visibility = View.GONE
            return
        }

        val entries = mutableListOf<PieEntry>()
        if (ingresos > 0) entries.add(PieEntry(ingresos.toFloat(), "Ingresos"))
        if (gastos > 0) entries.add(PieEntry(gastos.toFloat(), "Gastos"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.holeRadius = 40f
        binding.pieChart.setHoleColor(Color.TRANSPARENT)
        binding.pieChart.legend.isEnabled = true
        binding.pieChart.animateY(800)
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}