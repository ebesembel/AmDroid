# ⚡ AmDroid Pro - Resumen del Proyecto y Estado Actual

**AmDroid** es un reproductor de música retro para Android inspirado en el clásico Winamp, diseñado con arquitectura moderna en **Kotlin** y **Jetpack Compose**, con motor de ecualización gráfica de 10 bandas, analizador de espectro en tiempo real y controles nativos en segundo plano.

* **Repositorio GitHub:** [https://github.com/ebesembel/AmDroid](https://github.com/ebesembel/AmDroid)
* **Rama Principal:** `main`
* **Versión Actual:** `v1.5.0` (Código de versión: 6)
* **Archivo APK Listo:** `AmDroid.apk` (en la raíz del proyecto)

---

## 🎨 Identidad Visual y Logotipo Oficial
* **Icono de App:** Fondo titanio oscuro (`#0D1117`) con rayo delgado azul eléctrico (`#38BDF8` / `#0284C7`) flanqueado por las iniciales de autor: **`E`** (izquierda) y **`B`** (derecha).
* **Insignia en Cabeceras:** Componente vectorial `AmDroidLogoBadge` en alta resolución (sustituye completamente los emojis estándar del sistema).
* **Paleta de Colores Studio Pro:** 
  * Superficies: Titanio oscuro cepillado y obsidiana (`#0B0E14`, `#161C27`, `#0E121A`).
  * Acentos Neón: Cyan brillante (`#00F0FF`), Azul eléctrico (`#0284C7`), Azul cobalto profundo (`#0369A1`).
  * Pantalla LCD: Fondo negro azulado (`#05080E`) con dígitos LED en cyan glacial brillante.
  * Bordes: Marcos de precisión láser con degradado cyan-azul y resplandor ambiental (*ambient underglow*).

---

## 🚀 Historial de Versiones y Evolución

### v1.0.0 — Arquitectura Base
* Creación de la estructura del reproductor en Kotlin + Compose.
* Motor de audio nativo `MediaPlayer` con servicio en primer plano (`AudioPlaybackService`).
* Analizador de espectro FFT de 16 bandas y osciloscopio en tiempo real (`VisualizerManager`).
* Ecualizador gráfico clásico (`EqualizerManager`).
* Automatización de compilación en la nube mediante GitHub Actions (`.github/workflows/build-apk.yml`).

### v1.1.0 — Traducción al Español, Ajustes de Audio y UX Táctil
* Interfaz 100% en español (`TRAN` / `REST`, `VOLUMEN`, `POSICIÓN`, `ECUAL`, `LISTA`, `ALEAT`, `REPET`).
* Curva de sonido V-Shape por defecto con mayor pegada en sub-bajos (60Hz / 170Hz) y brillo cristalino en agudos (12kHz - 16kHz) hasta $\pm 15\text{ dB}$.
* Sliders táctiles optimizados con botones de paso milimétrico `▲` y `▼` para ajuste fino de 1 dB y doble toque para reiniciar a 0 dB.

### v1.2.0 — Controles Nativos en la Cortina de Android
* Implementación de notificación multimedia con `androidx.media.app.NotificationCompat.MediaStyle`.
* Botones nativos en la barra de tareas de Android: Anterior (`|◀`), Play/Pausa (`▶` / `⏸`), Siguiente (`▶|`).
* Botón de cierre definitivo (`✕`) que detiene la música, elimina la notificación y libera la memoria de la app.

### v1.3.0 — Rediseño Cyan Pro y Organización de la Lista
* Nuevo logotipo oficial integrado en el manifiesto y drawables vectoriales.
* Reemplazo de la paleta verde por la paleta Titanio & Cyan Eléctrico.
* Selector de ordenamiento en la lista de reproducción:
  * `📁 CARPETAS`: Clasificación por carpetas físicas.
  * `💿 ÁLBUM`: Orden por discos/álbumes.
  * `🔀 MEZCLAR`: Reordenamiento aleatorio inmediato.
  * `📋 TITULO`: Orden alfabético por nombre.

### v1.4.0 — Correcciones de Notificación y Carpetas Acordeón
* Logo oficial `E ⚡ B` renderizado a todo color en alta definición como carátula multimedia en la cortina y pantalla de bloqueo (`setLargeIcon`).
* Icono silueta vectorial `ic_stat_eb_logo.xml` para la barra de estado.
* Carpetas en formato acordeón: **solo permanece expandida la carpeta en reproducción** (con indicador `SONANDO`); las demás se pliegan con su contador de temas.
* Integración con `PlaybackStateCompat` y servicio `START_STICKY` para que la notificación no desaparezca al pausar o por ahorro de batería.

### v1.5.0 — Borde Láser y Eliminación de Emojis
* Sustitución del emoji amarillo `⚡` por el componente vectorial `AmDroidLogoBadge`.
* Marcos con degradado láser (`#00F0FF` $\rightarrow$ `#0284C7` $\rightarrow$ titanio) y sombra ambiental proyectada.

---

## 📁 Mapa de Archivos Clave

```
amdroid/
├── AmDroid.apk                                                <- Instalador directo más reciente
├── .github/workflows/build-apk.yml                           <- Compilador automático en la nube
├── app/src/main/
│   ├── AndroidManifest.xml                                   <- Permisos y configuración del servicio
│   ├── java/com/amdroid/player/
│   │   ├── MainActivity.kt                                   <- Actividad principal y permisos
│   │   ├── model/Track.kt                                    <- Modelo de datos de canciones y carpetas
│   │   ├── audio/
│   │   │   ├── AudioScanner.kt                               <- Escaneo de archivos del almacenamiento
│   │   │   ├── EqualizerManager.kt                           <- Lógica del ecualizador de 10 bandas
│   │   │   └── VisualizerManager.kt                          <- Captura FFT y osciloscopio
│   │   ├── service/AudioPlaybackService.kt                   <- Servicio en segundo plano y cortina
│   │   ├── util/LogoBitmapHelper.kt                          <- Generador bitmap del logo E ⚡ B
│   │   └── ui/
│   │       ├── theme/Color.kt & Theme.kt                     <- Paleta Cyan Eléctrico / Titanio
│   │       └── components/
│   │           ├── AmDroidLogoBadge.kt                       <- Logo vectorial E ⚡ B en títulos
│   │           ├── AmDroidScreen.kt                          <- Contenedor principal
│   │           ├── MainWindow.kt                             <- Ventana del reproductor
│   │           ├── EqualizerWindow.kt                        <- Ventana del ecualizador
│   │           ├── PlaylistWindow.kt                         <- Lista con carpetas acordeón
│   │           ├── RetroLcdDisplay.kt                        <- Pantalla LCD digital
│   │           ├── RetroSliders.kt                           <- Deslizadores táctiles con botones ▲ ▼
│   │           ├── RetroButtons.kt                           <- Botonera clásica
│   │           ├── RetroWindowBox.kt                         <- Marcos con borde láser y sombra
│   │           └── SpectrumVisualizerView.kt                 <- Analizador de espectro
│   └── res/
│       ├── drawable/ic_launcher_foreground.xml                <- Logo oficial de la app
│       └── drawable/ic_stat_eb_logo.xml                      <- Silueta para la barra de estado
```

---

## 📌 Para Continuar en la Próxima Sesión
1. El repositorio está 100% sincronizado con GitHub en la rama `main`.
2. Para compilar cualquier cambio futuro, solo hace falta hacer `git push` y GitHub Actions generará el nuevo APK automáticamente.
3. El archivo `AmDroid.apk` se encuentra descargado y listo para usar en tu PC.
