package com.controlnotas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.controlnotas.databinding.ActivityResumenBinding
import java.util.Locale

/**
 * ResumenActivity - Muestra el resumen estadístico del grupo completo.
 *
 * Presenta:
 *   - Total de estudiantes registrados
 *   - Cantidad de reprobados (nota definitiva < 3.0)
 *   - Cantidad de aprobados
 *   - Porcentajes de aprobación y reprobación
 *   - Lista detallada de todos los estudiantes con su resultado
 */
class ResumenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mostrarResumen()
        configurarBotones()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Refrescar datos al volver a esta pantalla
        mostrarResumen()
    }

    // ─── Mostrar resumen ─────────────────────────────────────────────────────

    private fun mostrarResumen() {
        val lista      = MainActivity.listaEstudiantes
        val total      = lista.size
        val reprobados = lista.count { !it.aprobado }
        val aprobados  = lista.count { it.aprobado }

        // Porcentajes (protegemos la división por cero)
        val pctReprobados = if (total > 0) (reprobados.toDouble() / total) * 100.0 else 0.0
        val pctAprobados  = if (total > 0) (aprobados.toDouble()  / total) * 100.0 else 0.0

        // ── Estadísticas ──
        binding.tvResumenTotal.text         = total.toString()
        binding.tvResumenReprobados.text     = reprobados.toString()
        binding.tvResumenAprobados.text      = aprobados.toString()
        binding.tvPorcentajeReprobados.text  = String.format(Locale.US, "%.1f%%", pctReprobados)
        binding.tvPorcentajeAprobados.text   = String.format(Locale.US, "%.1f%%", pctAprobados)

        // ── Lista detallada de estudiantes ──
        if (lista.isEmpty()) {
            binding.tvListaEstudiantes.text = getString(R.string.sin_estudiantes)
        } else {
            val sb = StringBuilder()
            lista.forEachIndexed { index, est ->
                val estado = if (est.aprobado) "✅ APROBADO" else "❌ REPROBADO"
                sb.append("${index + 1}. ${est.nombre}\n")
                sb.append("   Nota definitiva: ${String.format(Locale.US, "%.2f", est.notaDefinitiva)}  $estado\n")
                if (index < lista.size - 1) sb.append("\n")
            }
            binding.tvListaEstudiantes.text = sb.toString()
        }
    }

    // ─── Botones ─────────────────────────────────────────────────────────────

    private fun configurarBotones() {
        // Regresar a MainActivity para agregar más estudiantes
        binding.btnAgregarMas.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        // Reiniciar: borra todos los datos tras confirmar con el usuario
        binding.btnReiniciar.setOnClickListener {
            confirmarReinicio()
        }
    }

    /**
     * Muestra un diálogo de confirmación antes de borrar todos los datos,
     * ya que es una acción destructiva e irreversible dentro de la sesión.
     */
    private fun confirmarReinicio() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialogo_reiniciar_titulo))
            .setMessage(getString(R.string.dialogo_reiniciar_mensaje))
            .setPositiveButton(getString(R.string.dialogo_si)) { _, _ ->
                MainActivity.listaEstudiantes.clear()
                mostrarResumen()
            }
            .setNegativeButton(getString(R.string.dialogo_no), null)
            .show()
    }
}
