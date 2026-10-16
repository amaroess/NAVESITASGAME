package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import puppy.code.audio.GestorAudio;
import puppy.code.recursos.RecursosJuego;

public class SpaceNavigation extends Game {
    public static final float ANCHO = 1200;
    public static final float ALTO = 800;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer figuras;
    private RecursosJuego recursos;
    private Preferences preferencias;
    private int highScore;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        figuras = new ShapeRenderer();
        recursos = new RecursosJuego();
        preferencias = Gdx.app.getPreferences("SpaceNavigation");
        highScore = preferencias.getInteger("highScore", 0);
        GestorAudio.getInstancia().cargar();
        setScreen(new PantallaMenu(this));
    }

    public void cambiarPantalla(Screen nueva) {
        Screen anterior = getScreen();
        setScreen(nueva);
        if (anterior != null) anterior.dispose();
    }

    public void registrarPuntaje(int puntaje) {
        if (puntaje > highScore) {
            highScore = puntaje;
            preferencias.putInteger("highScore", highScore).flush();
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        GestorAudio.getInstancia().dispose();
        recursos.dispose();
        figuras.dispose();
        batch.dispose();
        font.dispose();
    }

    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() { return font; }
    public ShapeRenderer getFiguras() { return figuras; }
    public RecursosJuego getRecursos() { return recursos; }
    public int getHighScore() { return highScore; }
}
