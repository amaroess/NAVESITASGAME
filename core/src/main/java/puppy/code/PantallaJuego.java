package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import puppy.code.audio.GestorAudio;
import puppy.code.enemigos.Amenaza;
import puppy.code.enemigos.Asteroide;
import puppy.code.enemigos.Jefe;
import puppy.code.modelo.Entidad;
import puppy.code.modelo.Nave;
import puppy.code.modelo.Partida;
import puppy.code.modelo.PowerUp;
import puppy.code.modelo.Proyectil;

/** Traduce controles a acciones del modelo y dibuja el resultado. */
public final class PantallaJuego extends PantallaBase {
    private static final float PASO = 1f / 120f;
    private final Partida partida;
    private float acumulador;
    private float tiempoVisual;
    private boolean pausado;

    public PantallaJuego(SpaceNavigation game) { this(game, new Partida()); }

    PantallaJuego(SpaceNavigation game, Partida partida) {
        super(game);
        this.partida = partida;
    }

    @Override
    public void show() { GestorAudio.getInstancia().iniciarMusica(); }

    @Override
    public void hide() { GestorAudio.getInstancia().pausarMusica(); }

    @Override
    public void pause() { establecerPausa(true); }

    private void establecerPausa(boolean valor) {
        pausado = valor;
        acumulador = 0;
        if (pausado) GestorAudio.getInstancia().pausarMusica();
        else GestorAudio.getInstancia().iniciarMusica();
    }

    @Override
    public void render(float delta) {
        gestionarAudio();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.cambiarPantalla(new PantallaMenu(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) establecerPausa(!pausado);
        if (!pausado) {
            float direccionX = eje(Input.Keys.RIGHT, Input.Keys.D, Input.Keys.LEFT, Input.Keys.A);
            float direccionY = eje(Input.Keys.UP, Input.Keys.W, Input.Keys.DOWN, Input.Keys.S);
            boolean disparando = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            // Pasos pequenos evitan que un proyectil atraviese enemigos entre fotogramas.
            acumulador += Math.min(delta, 0.25f);
            while (acumulador >= PASO && !partida.haTerminado()) {
                partida.actualizar(PASO, direccionX, direccionY, disparando);
                acumulador -= PASO;
            }
            tiempoVisual += delta;
        }
        Partida.Evento evento;
        while ((evento = partida.siguienteEvento()) != null) GestorAudio.getInstancia().reproducir(evento);
        if (partida.haTerminado()) {
            game.registrarPuntaje(partida.getPuntaje());
            game.cambiarPantalla(new PantallaGameOver(game, partida.getPuntaje(), partida.getRonda()));
            return;
        }
        dibujarFondo(pausado ? 0 : delta);
        dibujarIndicadores();
        dibujarEntidades();
        dibujarTextos();
    }

    private float eje(int positiva, int alternativaPositiva, int negativa, int alternativaNegativa) {
        float valor = Gdx.input.isKeyPressed(positiva) || Gdx.input.isKeyPressed(alternativaPositiva) ? 1 : 0;
        return valor - (Gdx.input.isKeyPressed(negativa) || Gdx.input.isKeyPressed(alternativaNegativa) ? 1 : 0);
    }

    private void dibujarIndicadores() {
        figuras.begin(ShapeRenderer.ShapeType.Filled);
        figuras.setColor(0.035f, 0.055f, 0.12f, 1);
        figuras.rect(0, Partida.ALTO, SpaceNavigation.ANCHO, 80);
        figuras.setColor(Color.CYAN);
        figuras.rect(0, Partida.ALTO, SpaceNavigation.ANCHO, 2);
        for (PowerUp mejora : partida.getMejoras()) {
            figuras.setColor(colorMejora(mejora.getTipo()));
            figuras.circle(mejora.getCentroX(), mejora.getCentroY(), 18, 24);
        }
        for (Amenaza amenaza : partida.getAmenazas()) {
            if (amenaza.getVidaMaxima() <= 1) continue;
            float y = Math.min(Partida.ALTO - 8, amenaza.getY() + amenaza.getAlto() + 5);
            figuras.setColor(Color.DARK_GRAY);
            figuras.rect(amenaza.getX(), y, amenaza.getAncho(), 4);
            figuras.setColor(amenaza instanceof Jefe ? Color.MAGENTA : Color.SCARLET);
            figuras.rect(amenaza.getX(), y, amenaza.getAncho() * amenaza.getVida() / amenaza.getVidaMaxima(), 4);
        }
        figuras.end();
        if (partida.getNave().getEscudoRestante() > 0) {
            figuras.begin(ShapeRenderer.ShapeType.Line);
            figuras.setColor(Color.CYAN);
            Nave nave = partida.getNave();
            figuras.circle(nave.getCentroX(), nave.getCentroY(), 36, 40);
            figuras.circle(nave.getCentroX(), nave.getCentroY(), 38, 40);
            figuras.end();
        }
    }

    private void dibujarEntidades() {
        batch.begin();
        for (Amenaza amenaza : partida.getAmenazas()) {
            Texture textura;
            boolean invertida = !(amenaza instanceof Asteroide);
            if (amenaza instanceof Asteroide) {
                textura = amenaza.getAncho() > 45 ? game.getRecursos().getAsteroideGrande() : game.getRecursos().getAsteroide();
                batch.setColor(Color.LIGHT_GRAY);
            } else {
                textura = game.getRecursos().getNave();
                batch.setColor(amenaza instanceof Jefe ? Color.MAGENTA : Color.SCARLET);
            }
            if (amenaza.estaImpactada()) batch.setColor(Color.WHITE);
            dibujarTextura(textura, amenaza, invertida);
        }
        for (Proyectil proyectil : partida.getProyectiles()) {
            boolean enemigo = proyectil.getEquipo() == Proyectil.Equipo.ENEMIGO;
            batch.setColor(enemigo ? Color.SCARLET : Color.CYAN);
            dibujarTextura(game.getRecursos().getProyectil(), proyectil, enemigo);
        }
        batch.setColor(Color.WHITE);
        Nave nave = partida.getNave();
        if (!nave.estaHerida() || ((int) (tiempoVisual * 14)) % 2 == 0) {
            dibujarTextura(game.getRecursos().getNave(), nave, false);
        }
        for (PowerUp mejora : partida.getMejoras()) {
            font.getData().setScale(1.4f);
            font.setColor(Color.BLACK);
            font.draw(batch, mejora.getTipo().getEtiqueta(), mejora.getX(), mejora.getY() + 26,
                mejora.getAncho(), com.badlogic.gdx.utils.Align.center, false);
        }
        batch.end();
    }

    private void dibujarTextura(Texture textura, Entidad entidad, boolean invertida) {
        batch.draw(textura, entidad.getX(), entidad.getY(), entidad.getAncho(), entidad.getAlto(),
            0, 0, textura.getWidth(), textura.getHeight(), false, invertida);
    }

    private Color colorMejora(PowerUp.Tipo tipo) {
        switch (tipo) {
            case VIDA: return Color.GREEN;
            case ESCUDO: return Color.CYAN;
            case DOBLE: return Color.YELLOW;
            case ABANICO: return Color.MAGENTA;
            default: return Color.WHITE;
        }
    }

    private void dibujarTextos() {
        batch.begin();
        Nave nave = partida.getNave();
        texto("VIDAS: " + nave.getVidas() + "/" + Nave.VIDAS_MAXIMAS, 22, 782, 1.3f, Color.GREEN);
        texto("PUNTAJE: " + partida.getPuntaje(), 285, 782, 1.3f, Color.WHITE);
        texto("OLEADA: " + partida.getRonda(), 625, 782, 1.3f, Color.CYAN);
        texto("RECORD: " + Math.max(game.getHighScore(), partida.getPuntaje()), 920, 782, 1.2f, Color.YELLOW);
        texto("ARMA: " + nave.getNombreArma(), 22, 748, 1.05f, Color.WHITE);
        texto("ENEMIGOS: " + partida.getAmenazas().size(), 370, 748, 1.05f, Color.LIGHT_GRAY);
        String escudo = nave.getEscudoRestante() > 0 ? String.format(java.util.Locale.ROOT, "ESCUDO: %.1fs", nave.getEscudoRestante()) : "ESCUDO: -";
        texto(escudo, 625, 748, 1.05f, Color.CYAN);
        texto(GestorAudio.getInstancia().estaSilenciado() ? "M: SONIDO OFF" : "M: SONIDO ON", 920, 748, 1.05f, Color.LIGHT_GRAY);
        texto("Flechas/WASD: mover   ESPACIO: disparar   P: pausa   ESC: menu", 18, 22, 0.9f, Color.LIGHT_GRAY);
        if (!pausado && !partida.getMensaje().isEmpty()) textoCentrado(partida.getMensaje(), 665, 1.3f, Color.YELLOW);
        if (!pausado && partida.getEsperaOleada() >= 0) {
            textoCentrado("OLEADA COMPLETADA", 445, 2, Color.CYAN);
            textoCentrado("Siguiente oleada en " + (int) Math.ceil(partida.getEsperaOleada()), 385, 1.4f, Color.WHITE);
        }
        batch.end();
        if (pausado) dibujarPausa();
    }

    private void dibujarPausa() {
        figuras.begin(ShapeRenderer.ShapeType.Filled);
        figuras.setColor(0.025f, 0.04f, 0.1f, 1);
        figuras.rect(220, 270, 760, 250);
        figuras.end();
        batch.begin();
        textoCentrado("PAUSA", 465, 3, Color.CYAN);
        textoCentrado("P: continuar   |   M: sonido   |   ESC: menu", 365, 1.3f, Color.WHITE);
        batch.end();
    }

    @Override
    public void dispose() { game.registrarPuntaje(partida.getPuntaje()); }
}
