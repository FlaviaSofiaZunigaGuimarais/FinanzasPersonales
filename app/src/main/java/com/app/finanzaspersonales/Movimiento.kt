package com.app.finanzaspersonales

data class Movimiento(
    val id: String = "",
    val tipo: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: String = "",
    val descripcion: String = ""
)