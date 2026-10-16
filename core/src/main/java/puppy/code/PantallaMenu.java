package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import puppy.code.audio.GestorAudio;

public final class PantallaMenu extends PantallaBase {
    public PantallaMenu(SpaceNavigation game) { super(game); }

    @Override
    public void show() { GestorAudio.getInstancia().pausarMusica(); }

    @Override
    public void render(float delta) {
        gestionarAudio();
        dibujarFondo(delta);
        batch.begin();
        textoCentrado("SPACE NAVIGATION", 635, 3.2f, Color.CYAN);
        textoCentrado("SUPERVIVENCIA POR OLEADAS", 575, 1.4f, Color.WHITE);
        textoCentrado("ENTER / ESPACIO o clic para jugar", 475, 1.6f, Color.YELLOW);
        textoCentrado("Flechas o WASD: mover   |   Mantener ESPACIO: disparar", 400, 1.2f, Color.WHITE);
        textoCentrado("P: pausa   |   M: sonido   |   ESC: volver al menu", 360, 1.2f, Color.LIGHT_GRAY);
        textoCentrado("MEJORAS:  + Vida    E Escudo    D Disparo doble    A Abanico", 280, 1.2f, Color.GREEN);
        textoCentrado("Recoge las mejoras que sueltan los enemigos. Hay un jefe cada 3 oleadas.", 240, 1.1f, Color.LIGHT_GRAY);
        textoCentrado("RECORD: " + game.getHighScore(), 150, 1.5f, Color.YELLOW);
        textoCentrado(GestorAudio.getInstancia().estaSilenciado() ? "SONIDO DESACTIVADO" : "SONIDO ACTIVADO", 95, 1, Color.LIGHT_GRAY);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            game.cambiarPantalla(new PantallaJuego(game));
        }
    }
}
