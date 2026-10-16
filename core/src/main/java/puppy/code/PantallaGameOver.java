package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import puppy.code.audio.GestorAudio;

public final class PantallaGameOver extends PantallaBase {
    private final int puntaje;
    private final int ronda;

    public PantallaGameOver(SpaceNavigation game, int puntaje, int ronda) {
        super(game);
        this.puntaje = puntaje;
        this.ronda = ronda;
    }

    @Override
    public void show() { GestorAudio.getInstancia().pausarMusica(); }

    @Override
    public void render(float delta) {
        gestionarAudio();
        dibujarFondo(delta);
        batch.begin();
        textoCentrado("GAME OVER", 570, 3.5f, Color.SCARLET);
        textoCentrado("PUNTAJE: " + puntaje + "     OLEADA: " + ronda, 460, 1.8f, Color.WHITE);
        textoCentrado("RECORD: " + game.getHighScore(), 390, 1.5f, Color.YELLOW);
        textoCentrado("ENTER / ESPACIO / R o clic: volver a jugar", 280, 1.3f, Color.CYAN);
        textoCentrado("ESC: menu principal", 225, 1.2f, Color.LIGHT_GRAY);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.cambiarPantalla(new PantallaMenu(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.justTouched()) {
            game.cambiarPantalla(new PantallaJuego(game));
        }
    }
}
