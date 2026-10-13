# Reglas ProGuard para Control de Notas
# Mantener clases del paquete principal
-keep class com.controlnotas.** { *; }

# Mantener data classes de Kotlin
-keepclassmembers class com.controlnotas.Estudiante {
    public *;
}
