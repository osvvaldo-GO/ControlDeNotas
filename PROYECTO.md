# Control de Notas — Documentación del Proyecto

## Descripción general

Aplicación Android desarrollada en **Kotlin** para la asignatura **Programación de Dispositivos Móviles**.  
Permite registrar las notas de varios estudiantes, calcular su nota definitiva de forma ponderada,
indicar si aprueba o reprueba, y mostrar un resumen estadístico del grupo.

---

## Datos del proyecto

| Campo              | Valor                          |
|--------------------|--------------------------------|
| Lenguaje           | Kotlin                         |
| Min SDK            | 24 (Android 7.0)               |
| Target / Compile   | SDK 34 (Android 14)            |
| Gradle             | 8.0                            |
| AGP                | 8.1.0                          |
| Namespace          | `com.controlnotas`             |
| View Binding       | Activado                       |
| Ruta del proyecto  | `C:\Users\OSWALDO\AndroidProjects\ControlDeNotas` |

---

## Estructura de archivos

```
ControlDeNotas/
├── build.gradle                          ← Configuración proyecto (sin allprojects)
├── settings.gradle                       ← Módulos + dependencyResolutionManagement
├── gradle.properties                     ← JVM args, AndroidX, parallel build
├── gradle/wrapper/
│   └── gradle-wrapper.properties         ← Gradle 8.0
├── PROYECTO.md                           ← Este archivo
└── app/
    ├── build.gradle                      ← Config módulo: plugins, deps, sourceSets
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml           ← 3 Activities declaradas (sin ic_launcher)
        ├── kotlin/com/controlnotas/
        │   ├── Estudiante.kt             ← Data class del modelo
        │   ├── MainActivity.kt           ← Pantalla 1: formulario + validación
        │   ├── ResultadoActivity.kt      ← Pantalla 2: resultado individual
        │   └── ResumenActivity.kt        ← Pantalla 3: estadísticas del grupo
        └── res/
            ├── layout/
            │   ├── activity_main.xml     ← Formulario de notas + contadores
            │   ├── activity_resultado.xml← Desglose ponderado + estado
            │   └── activity_resumen.xml  ← Estadísticas + lista estudiantes
            ├── drawable/
            │   └── bg_input.xml          ← Fondo redondeado para EditText
            └── values/
                ├── strings.xml           ← Todas las cadenas de texto
                ├── colors.xml            ← Paleta de colores
                └── themes.xml            ← Theme.ControlNotas (MaterialComponents)
```

---

## Lógica de negocio

### Porcentajes de las notas

| Nota   | Porcentaje | Constante en código              |
|--------|------------|----------------------------------|
| Nota 1 | 25%        | `PORCENTAJE_NOTA_1 = 0.25`       |
| Nota 2 | 25%        | `PORCENTAJE_NOTA_2 = 0.25`       |
| Nota 3 | 30%        | `PORCENTAJE_NOTA_3 = 0.30`       |
| Nota 4 | 20%        | `PORCENTAJE_NOTA_4 = 0.20`       |
| Total  | 100%       |                                  |

### Fórmula de nota definitiva

```
notaDefinitiva = (nota1 × 0.25) + (nota2 × 0.25) + (nota3 × 0.30) + (nota4 × 0.20)
```

### Reglas de validación

- Cada nota debe estar en el rango **[0.0 — 5.0]**
- Si una nota está fuera del rango → Toast de error, no se procesa
- El nombre del estudiante no puede estar vacío

### Criterio de aprobación

- Nota definitiva **≥ 3.0** → APROBADO ✅
- Nota definitiva **< 3.0** → REPROBADO ❌

---

## Flujo de navegación entre Activities

```
MainActivity  ──────────────────────────────────────────────┐
    │                                                        │
    │ (btnCalcular → datos válidos)                          │ (btnVerResumen)
    ▼                                                        ▼
ResultadoActivity                                     ResumenActivity
    │                                                        │
    │ (btnOtroEstudiante)           (btnIrResumen) ──────────┘
    └──────────────────── MainActivity ◄─── (btnAgregarMas)
```

---

## Estado global compartido

La lista de estudiantes se almacena en el **companion object de MainActivity**:

```kotlin
companion object {
    val listaEstudiantes = mutableListOf<Estudiante>()
}
```

Esto permite que `ResumenActivity` acceda a todos los registros sin necesidad
de pasar datos por Intent. Al presionar "Reiniciar", la lista se limpia con
`listaEstudiantes.clear()` tras confirmación del usuario.

> ⚠️ **Nota para escalamiento:** Este enfoque es adecuado para una app de sesión única.
> Si se requiere persistencia entre sesiones, migrar a **Room Database** o **SharedPreferences**.

---

## Notas técnicas importantes

### sourceSets en app/build.gradle
Los archivos `.kt` están en `src/main/kotlin/` (no en `java/`).
Por eso el `app/build.gradle` incluye:

```groovy
sourceSets {
    main {
        java.srcDirs = ['src/main/kotlin']
    }
}
```

### Sin ic_launcher
El `AndroidManifest.xml` **no** referencia `@mipmap/ic_launcher` porque los recursos
de mipmap no fueron generados. Si se desea agregar ícono personalizado:
1. Crear carpetas `res/mipmap-hdpi/`, `mipmap-mdpi/`, `mipmap-xhdpi/`, `mipmap-xxhdpi/`, `mipmap-xxxhdpi/`
2. Agregar `ic_launcher.png` en cada una con las resoluciones correctas
3. Restaurar en el Manifest: `android:icon="@mipmap/ic_launcher"`

### Ruta del proyecto
El proyecto **debe** estar en una ruta sin caracteres especiales (tildes, ñ, espacios).
Gradle falla con rutas como `MIS PROYECTOS ACADÉMICOS` por la É.
Ruta actual correcta: `C:\Users\OSWALDO\AndroidProjects\ControlDeNotas`

---

## Posibles mejoras y escalamiento

| Mejora                          | Tecnología sugerida              |
|---------------------------------|----------------------------------|
| Persistencia de datos           | Room Database / SQLite           |
| Múltiples materias              | Agregar campo "materia" al modelo|
| Exportar resultados             | FileProvider + CSV/PDF           |
| Cambiar porcentajes dinámicamente| ViewModel + LiveData            |
| Interfaz moderna                | Jetpack Compose                  |
| Pruebas automatizadas           | JUnit + Espresso                 |
| Ícono de la app                 | Image Asset Studio (Android Studio)|

---

## Cómo abrir y ejecutar

1. Abrir **Android Studio**
2. **Open** → seleccionar `C:\Users\OSWALDO\AndroidProjects\ControlDeNotas`
3. Esperar sincronización de Gradle
4. Conectar dispositivo Android con **Depuración USB** activada
5. Seleccionar el dispositivo en la barra superior
6. Presionar **▶ Run**

### Requisitos del dispositivo de prueba
- Android 7.0 o superior (minSdk 24)
- Depuración USB activada
- Probado en: **Xiaomi Redmi Note 9S**

---

*Documentación generada con Kiro IDE — Octubre 2026*
