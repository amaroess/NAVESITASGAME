# Space Navigation

Juego de naves en Java y libGDX, organizado en un modulo de logica (`core`) y un lanzador de escritorio (`lwjgl3`).

## Ejecutar

Usar JDK 11. Desde la carpeta del proyecto:

PowerShell:

```powershell
.\gradlew.bat lwjgl3:run
```

Git Bash:

```bash
./gradlew lwjgl3:run
```

La primera ejecucion descarga las dependencias con el wrapper de Gradle incluido.

## Controles

| Accion | Control |
| --- | --- |
| Mover | Flechas o WASD |
| Disparar | Mantener Espacio |
| Pausar / continuar | P |
| Activar / desactivar sonido | M |
| Volver al menu | Esc |
| Iniciar / reiniciar | Enter, Espacio o clic |

## Partida

- Comienza con 3 vidas; se pueden recuperar hasta 5.
- Hay asteroides que rebotan y naves enemigas que descienden en zigzag y disparan.
- Cada tres oleadas aparece un jefe con rafagas y mas resistencia.
- Al completar una oleada se conservan nave, vidas, arma y escudo.
- Los enemigos pueden soltar mejoras: **+** vida, **E** escudo de 6 segundos, **D** disparo doble y **A** abanico.
- La segunda baja siempre ofrece un disparo doble; las mejoras posteriores son aleatorias.
- El record y la preferencia de sonido se guardan entre sesiones.
- Asteroide: 10 puntos. Nave enemiga: 35 puntos. Jefe: 500 puntos.

## Organizacion del codigo

`Partida` coordina las reglas y las colisiones sin depender de texturas, ventanas ni audio.
`PantallaJuego` interpreta los controles y dibuja el modelo con un paso de simulacion fijo.
Las entidades utilizan segundos para sus movimientos y temporizadores. Las pantallas comparten
un mundo virtual con `FitViewport`, recursos graficos y audio administrados durante la vida de la aplicacion.

| Requisito | Implementacion |
| --- | --- |
| Clase abstracta e interfaz | `Amenaza` / `Entidad` y `Danable` / `EstrategiaDisparo` |
| Encapsulamiento | Estado privado, acciones de dominio y colecciones de solo lectura en `Partida` |
| Singleton | [GestorAudio](core/src/main/java/puppy/code/audio/GestorAudio.java) |
| Strategy | [EstrategiaDisparo](core/src/main/java/puppy/code/armas/EstrategiaDisparo.java), implementada por disparos simple, doble y abanico |
| Template Method | [Amenaza.actualizar](core/src/main/java/puppy/code/enemigos/Amenaza.java), especializado por asteroide, nave enemiga y jefe |
| Builder | [ConfiguracionOleada.Builder](core/src/main/java/puppy/code/modelo/ConfiguracionOleada.java) |

Las texturas se cargan una vez mediante `RecursosJuego` y se liberan al cerrar el juego.
`GestorAudio` es el unico propietario de la musica y los efectos.

## Verificacion

Pruebas de reglas, movimiento, armas, danio, colisiones y progresion:

```bash
./gradlew core:test
```

Prueba grafica opcional en una ventana oculta (requiere OpenGL):

```bash
./gradlew lwjgl3:smokeTest
```

Guarda capturas de menu, partida, pausa y Game Over en `lwjgl3/build/smoke/`.
Usa audio desactivado y preferencias aisladas de las partidas reales.

Generar un JAR con sus dependencias y recursos:

```bash
./gradlew lwjgl3:jar
```

El archivo se genera en `lwjgl3/build/libs/SpaceNav2024-1.0.0.jar`.
