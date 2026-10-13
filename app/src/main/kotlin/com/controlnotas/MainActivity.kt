package com.controlnotas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.controlnotas.databinding.ActivityMainBinding

/**
 * MainActivity - Pantalla principal de la aplicación Control de Notas.
 *
 * Permite ingresar el nombre del estudiante y sus 4 notas con porcentajes:
 *   Nota 1: 25%
 *   Nota 2: 25%
 *   Nota 3: 30%
 *   Nota 4: 20%
 *
 * Valida que cada nota esté en el rango [0.0, 5.0].
 * Mantiene un contador global de estudiantes registrados y reprobados.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Porcentajes de cada nota (deben sumar 1.0)
    companion object {
        const val PORCENTAJE_NOTA_1 = 0.25
        const val PORCENTAJE_NOTA_2 = 0.25
        const val PORCENTAJE_NOTA_3 = 0.30
        const val PORCENTAJE_NOTA_4 = 0.20
        const val NOTA_MINIMA_APROBACION = 3.0
        const val NOTA_MINIMA = 0.0
        const val NOTA_MAXIMA = 5.0

        // Claves para pasar datos entre Activities
        const val EXTRA_NOMBRE     = "extra_nombre"
        const val EXTRA_NOTA1      = "extra_nota1"
        const val EXTRA_NOTA2      = "extra_nota2"
        const val EXTRA_NOTA3      = "extra_nota3"
        const val EXTRA_NOTA4      = "extra_nota4"

        // Estado global compartido entre activities (lista de estudiantes)
        val listaEstudiantes = mutableListOf<Estudiante>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actualizarContadores()
        configurarBotones()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar contadores cada vez que se regresa a esta pantalla
        actualizarContadores()
    }

    // ─── Configuración de botones ────────────────────────────────────────────

    private fun configurarBotones() {

        // Calcular nota del estudiante actual
        binding.btnCalcular.setOnClickListener {
            calcularNota()
        }

        // Limpiar formulario para un nuevo estudiante
        binding.btnNuevoEstudiante.setOnClickListener {
            limpiarFormulario()
        }

        // Ir directamente al resumen general
        binding.btnVerResumen.setOnClickListener {
            irAResumen()
        }
    }

    // ─── Lógica principal: validar y calcular ────────────────────────────────

    private fun calcularNota() {
        val nombre = binding.etNombre.text.toString().trim()

        // Validar nombre
        if (nombre.isEmpty()) {
            binding.etNombre.error = getString(R.string.error_nombre_vacio)
            binding.etNombre.requestFocus()
            return
        }

        // Leer y validar las 4 notas
        val nota1 = leerYValidarNota(binding.etNota1.text.toString(), "Nota 1") ?: return
        val nota2 = leerYValidarNota(binding.etNota2.text.toString(), "Nota 2") ?: return
        val nota3 = leerYValidarNota(binding.etNota3.text.toString(), "Nota 3") ?: return
        val nota4 = leerYValidarNota(binding.etNota4.text.toString(), "Nota 4") ?: return

        // Calcular nota definitiva ponderada
        val notaDefinitiva = (nota1 * PORCENTAJE_NOTA_1) +
                             (nota2 * PORCENTAJE_NOTA_2) +
                             (nota3 * PORCENTAJE_NOTA_3) +
                             (nota4 * PORCENTAJE_NOTA_4)

        // Registrar el estudiante en la lista global
        val estudiante = Estudiante(
            nombre        = nombre,
            nota1         = nota1,
            nota2         = nota2,
            nota3         = nota3,
            nota4         = nota4,
            notaDefinitiva = notaDefinitiva
        )
        listaEstudiantes.add(estudiante)

        // Actualizar contadores visibles
        actualizarContadores()

        // Navegar a la pantalla de resultado
        val intent = Intent(this, ResultadoActivity::class.java).apply {
            putExtra(EXTRA_NOMBRE, nombre)
            putExtra(EXTRA_NOTA1,  nota1)
            putExtra(EXTRA_NOTA2,  nota2)
            putExtra(EXTRA_NOTA3,  nota3)
            putExtra(EXTRA_NOTA4,  nota4)
        }
        startActivity(intent)
    }

    /**
     * Lee el texto de un campo de nota, lo convierte a Double y valida
     * que esté en el rango [0.0, 5.0].
     *
     * @return el valor Double si es válido, null si hay error (ya muestra Toast).
     */
    private fun leerYValidarNota(texto: String, etiqueta: String): Double? {
        if (texto.isEmpty()) {
            mostrarError("$etiqueta: ${getString(R.string.error_campo_vacio)}")
            return null
        }

        val valor = texto.toDoubleOrNull()
        if (valor == null) {
            mostrarError("$etiqueta: ${getString(R.string.error_numero_invalido)}")
            return null
        }

        if (valor < NOTA_MINIMA || valor > NOTA_MAXIMA) {
            mostrarError("$etiqueta: ${getString(R.string.error_rango_nota)}")
            return null
        }

        return valor
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun actualizarContadores() {
        val total      = listaEstudiantes.size
        val reprobados = listaEstudiantes.count { it.notaDefinitiva < NOTA_MINIMA_APROBACION }
        binding.tvTotalEstudiantes.text = total.toString()
        binding.tvReprobados.text       = reprobados.toString()
    }

    private fun limpiarFormulario() {
        binding.etNombre.setText("")
        binding.etNota1.setText("")
        binding.etNota2.setText("")
        binding.etNota3.setText("")
        binding.etNota4.setText("")
        binding.etNombre.requestFocus()
    }

    private fun irAResumen() {
        if (listaEstudiantes.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_sin_datos), Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(this, ResumenActivity::class.java))
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
