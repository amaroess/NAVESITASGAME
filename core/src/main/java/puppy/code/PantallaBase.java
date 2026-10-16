package puppy.code;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import puppy.code.audio.GestorAudio;

/** Presentacion comun: mundo virtual fijo y escalado que conserva proporciones. */
public abstract class PantallaBase extends ScreenAdapter {
    protected final SpaceNavigation game;
    protected final SpriteBatch batch;
    protected final BitmapFont font;
    protected final ShapeRenderer figuras;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(SpaceNavigation.ANCHO, SpaceNavigation.ALTO, camera);
    private final float[] estrellasX = new float[90];
    private final float[] estrellasY = new float[90];
    private final float[] velocidades = new float[90];

    protected PantallaBase(SpaceNavigation game) {
        this.game = game;
        batch = game.getBatch();
        font = game.getFont();
        figuras = game.getFiguras();
        Random azar = new Random(19);
        for (int i = 0; i < estrellasX.length; i++) {
            estrellasX[i] = azar.nextFloat() * SpaceNavigation.ANCHO;
            estrellasY[i] = azar.nextFloat() * SpaceNavigation.ALTO;
            velocidades[i] = 12 + azar.nextFloat() * 36;
        }
    }

    protected void gestionarAudio() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) GestorAudio.getInstancia().alternarSilencio();
    }

    protected void dibujarFondo(float delta) {
        ScreenUtils.clear(0.015f, 0.025f, 0.065f, 1);
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        figuras.setProjectionMatrix(camera.combined);
        figuras.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < estrellasX.length; i++) {
            estrellasY[i] -= velocidades[i] * Math.min(delta, 0.1f);
            if (estrellasY[i] < 0) estrellasY[i] += SpaceNavigation.ALTO;
            float brillo = 0.35f + velocidades[i] / 80;
            figuras.setColor(brillo * 0.7f, brillo * 0.85f, brillo, 1);
            float tamano = velocidades[i] > 35 ? 2 : 1;
            figuras.rect(estrellasX[i], estrellasY[i], tamano, tamano);
        }
        figuras.end();
    }

    protected void texto(String texto, float x, float y, float escala, Color color) {
        font.getData().setScale(escala);
        font.setColor(color);
        font.draw(batch, texto, x, y);
    }

    protected void textoCentrado(String texto, float y, float escala, Color color) {
        font.getData().setScale(escala);
        font.setColor(color);
        font.draw(batch, texto, 0, y, SpaceNavigation.ANCHO, Align.center, false);
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }
}
