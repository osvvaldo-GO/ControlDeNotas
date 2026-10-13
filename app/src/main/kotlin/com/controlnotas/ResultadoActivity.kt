package com.controlnotas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.controlnotas.databinding.ActivityResultadoBinding
import java.util.Locale

/**
 * ResultadoActivity - Muestra el resultado individual de un estudiante.
 *
 * Recibe las 4 notas mediante Extras, muestra:
 *   - Nombre del estudiante
 *   - Cada nota con su aporte ponderado
 *   - Nota definitiva
 *   - Estado: APROBADO o REPROBADO
 */
class ResultadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Habilitar botón "atrás" en la barra de acción
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mostrarResultado()
        configurarBotones()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // ─── Mostrar resultado ───────────────────────────────────────────────────

    private fun mostrarResultado() {
        // Leer datos recibidos desde MainActivity
        val nombre = intent.getStringExtra(MainActivity.EXTRA_NOMBRE) ?: "Estudiante"
        val nota1  = intent.getDoubleExtra(MainActivity.EXTRA_NOTA1, 0.0)
        val nota2  = intent.getDoubleExtra(MainActivity.EXTRA_NOTA2, 0.0)
        val nota3  = intent.getDoubleExtra(MainActivity.EXTRA_NOTA3, 0.0)
        val nota4  = intent.getDoubleExtra(MainActivity.EXTRA_NOTA4, 0.0)

        // Calcular aportes ponderados
        val pond1 = nota1 * MainActivity.PORCENTAJE_NOTA_1
        val pond2 = nota2 * MainActivity.PORCENTAJE_NOTA_2
        val pond3 = nota3 * MainActivity.PORCENTAJE_NOTA_3
        val pond4 = nota4 * MainActivity.PORCENTAJE_NOTA_4
        val definitiva = pond1 + pond2 + pond3 + pond4

        val aprobado = definitiva >= MainActivity.NOTA_MINIMA_APROBACION

        // ── Nombre ──
        binding.tvNombreEstudiante.text = nombre

        // ── Notas y aportes ponderados ──
        binding.tvNota1.text     = formatNota(nota1)
        binding.tvPonderada1.text = "= ${formatNota(pond1)}"

        binding.tvNota2.text     = formatNota(nota2)
        binding.tvPonderada2.text = "= ${formatNota(pond2)}"

        binding.tvNota3.text     = formatNota(nota3)
        binding.tvPonderada3.text = "= ${formatNota(pond3)}"

        binding.tvNota4.text     = formatNota(nota4)
        binding.tvPonderada4.text = "= ${formatNota(pond4)}"

        // ── Nota definitiva ──
        binding.tvNotaDefinitiva.text = formatNota(definitiva)

        // ── Estado y colores ──
        if (aprobado) {
            binding.tvIconoEstado.text = "🎓"
            binding.tvEstado.text = getString(R.string.estado_aprobado)
            binding.cardEstado.setCardBackgroundColor(getColor(R.color.verde))
            binding.tvNotaDefinitiva.setTextColor(getColor(R.color.verde))
        } else {
            binding.tvIconoEstado.text = "❌"
            binding.tvEstado.text = getString(R.string.estado_reprobado)
            binding.cardEstado.setCardBackgroundColor(getColor(R.color.rojo))
            binding.tvNotaDefinitiva.setTextColor(getColor(R.color.rojo))
        }
    }

    // ─── Botones ─────────────────────────────────────────────────────────────

    private fun configurarBotones() {
        // Regresar a MainActivity para ingresar otro estudiante
        binding.btnOtroEstudiante.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                // Limpiar el back stack para evitar acumular Activities
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        // Ir a la pantalla de resumen general
        binding.btnIrResumen.setOnClickListener {
            startActivity(Intent(this, ResumenActivity::class.java))
        }
    }

    // ─── Utilidades ──────────────────────────────────────────────────────────

    /** Formatea un Double a 2 decimales con locale fijo para consistencia. */
    private fun formatNota(valor: Double): String =
        String.format(Locale.US, "%.2f", valor)
}
