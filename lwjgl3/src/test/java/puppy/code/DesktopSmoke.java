package puppy.code;

import java.util.Random;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import puppy.code.armas.DisparoAbanico;
import puppy.code.modelo.ConfiguracionOleada;
import puppy.code.modelo.Partida;

/** Prueba opcional de escritorio: requiere un contexto OpenGL disponible. */
public final class DesktopSmoke {
    private DesktopSmoke() { }

    public static void main(String[] args) {
        String salida = System.getProperty("smoke.output", "build/smoke");
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Space Navigation - verificacion");
        config.setWindowedMode(960, 640);
        config.setInitialVisible(false);
        config.disableAudio(true);
        config.useVsync(false);
        config.setForegroundFPS(120);
        config.setPreferencesConfig(salida + "/preferences", Files.FileType.Absolute);
        new Lwjgl3Application(new AplicacionPrueba(salida), config);
    }

    private static final class AplicacionPrueba extends SpaceNavigation {
        private final String salida;
        private int fotograma;
        private Partida partida;

        private AplicacionPrueba(String salida) { this.salida = salida; }

        @Override
        public void render() {
            fotograma++;
            if (fotograma == 4) {
                partida = new Partida(new Random(7), new ConfiguracionOleada.Builder()
                    .numero(3).cantidadAsteroides(6).cantidadNavesEnemigas(2)
                    .velocidadEnemigos(100).incluirJefe(true).build());
                cambiarPantalla(new PantallaJuego(this, partida));
            }
            if (fotograma >= 5 && fotograma < 120) {
                partida.actualizar(1f / 60, fotograma < 60 ? 1 : -1, 0, true);
            }
            if (fotograma == 60) partida.getNave().cambiarArma(new DisparoAbanico());
            if (fotograma == 90) partida.getNave().activarEscudo(6);
            if (fotograma == 120) getScreen().pause();
            if (fotograma == 127) cambiarPantalla(new PantallaGameOver(this, partida.getPuntaje(), 3));
            if (fotograma == 131) cambiarPantalla(new PantallaMenu(this));
            super.render();
            if (fotograma == 2) capturar("menu.png");
            if (fotograma == 110) capturar("partida.png");
            if (fotograma == 123) capturar("pausa.png");
            if (fotograma == 129) capturar("game-over.png");
            if (fotograma == 133) {
                Gdx.app.log("DesktopSmoke", "OK: menu, partida, jefe, escudo, pausa y game over renderizados");
                Gdx.app.exit();
            }
        }

        private void capturar(String archivo) {
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0,
                Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
            try {
                PixmapIO.writePNG(Gdx.files.absolute(salida + "/" + archivo), pixmap, -1, true);
            } finally {
                pixmap.dispose();
            }
        }
    }
}
