package com.controlnotas

/**
 * Modelo de datos que representa a un estudiante con sus 4 notas
 * y la nota definitiva calculada de forma ponderada.
 *
 * Porcentajes aplicados:
 *   nota1 → 25%
 *   nota2 → 25%
 *   nota3 → 30%
 *   nota4 → 20%
 */
data class Estudiante(
    val nombre: String,
    val nota1: Double,
    val nota2: Double,
    val nota3: Double,
    val nota4: Double,
    val notaDefinitiva: Double
) {
    /** Devuelve true si el estudiante aprobó la materia (nota >= 3.0) */
    val aprobado: Boolean
        get() = notaDefinitiva >= MainActivity.NOTA_MINIMA_APROBACION
}
