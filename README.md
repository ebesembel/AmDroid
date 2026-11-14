# AmDroid 🎛️⚡
### El legendario reproductor retro estilo Winamp para Android

**AmDroid** recrea la experiencia clásica de los reproductores de música de los años 90 y 2000 (Winamp) en Android moderno, utilizando **Kotlin** y **Jetpack Compose**.

---

## 🚀 Características Principales

1. **Ventana Principal (Main Window):**
   * Pantalla LCD verde neón con marquesina de título en scroll.
   * Contador digital LED con alternancia entre tiempo transcurrido (`ELAP`) y tiempo restante (`REM`).
   * Indicadores de calidad de audio (`320 kbps`, `44 kHz`) y luz de `STEREO`.
   * **Analizador de Espectro en Tiempo Real:** 16 bandas de frecuencia con caída gravitatoria de picos y alternancia a modo **Osciloscopio**.
   * Controles de reproducción retro (`|<<`, `>`, `||`, `■`, `>>|`, `⏏`).
   * Control de Volumen y Balance estéreo (L/R).

2. **Ecualizador Gráfico de 10 Bandas (Equalizer Window):**
   * Control de ganancia Preamp (-12dB a +12dB).
   * 10 bandas de frecuencia clásicas (60Hz, 170Hz, 310Hz, 600Hz, 1kHz, 3kHz, 6kHz, 12kHz, 14kHz, 16kHz).
   * Presets icónicos (Flat, Rock, Full Bass, Pop, Techno, Classical, Reggae, Ska, Party).
   * Interruptor general ON/OFF.

3. **Lista de Reproducción (Playlist Editor):**
   * Escaneo automático del almacenamiento del dispositivo.
   * Pista activa resaltada en azul clásico `#0000A0`.
   * Modos **Shuffle** (aleatorio) y **Repeat** (repetir).
   * Duración total acumulada y contador de pistas.

4. **Reproducción en Segundo Plano:**
   * Servicio en primer plano (`Foreground Service`) que mantiene la música sonando con la pantalla bloqueada o en otras aplicaciones.
   * Notificación multimedia en la barra de Android con controles directos.

---

## 📦 Cómo generar tu archivo APK con GitHub Actions (Sin instalar nada en tu PC)

1. Crea un repositorio en tu cuenta de [GitHub](https://github.com) llamado `amdroid`.
2. Sube esta carpeta a tu repositorio.
3. Ve a la pestaña **Actions** en tu GitHub: verás el flujo `Build AmDroid APK` ejecutándose en los servidores de GitHub.
4. Al terminar (toma unos 2 o 3 minutos), en la sección **Artifacts** encontrarás el archivo `AmDroid-v1.0-Debug-APK` listo para descargar e instalar en tu teléfono.
