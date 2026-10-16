package puppy.code.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import puppy.code.modelo.Partida.Evento;

/** Singleton: una sola instancia administra los recursos de audio del juego. */
public final class GestorAudio implements Disposable {
    private static GestorAudio instancia;
    private Music musica;
    private Sound disparo;
    private Sound golpe;
    private Sound explosion;
    private boolean cargado;
    private boolean silenciado;
    private boolean musicaSolicitada;

    private GestorAudio() { }

    public static synchronized GestorAudio getInstancia() {
        if (instancia == null) instancia = new GestorAudio();
        return instancia;
    }

    public void cargar() {
        if (cargado) return;
        musica = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        disparo = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));
        golpe = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        musica.setLooping(true);
        musica.setVolume(0.25f);
        silenciado = Gdx.app.getPreferences("SpaceNavigation").getBoolean("silenciado", false);
        cargado = true;
    }

    public void iniciarMusica() {
        musicaSolicitada = true;
        if (cargado && !silenciado && !musica.isPlaying()) musica.play();
    }

    public void pausarMusica() {
        musicaSolicitada = false;
        if (cargado) musica.pause();
    }

    public void alternarSilencio() {
        silenciado = !silenciado;
        if (silenciado) {
            musica.pause();
            disparo.stop();
            golpe.stop();
            explosion.stop();
        } else if (musicaSolicitada) {
            musica.play();
        }
        Gdx.app.getPreferences("SpaceNavigation").putBoolean("silenciado", silenciado).flush();
    }

    public void reproducir(Evento evento) {
        if (!cargado || silenciado) return;
        switch (evento) {
            case DISPARO: disparo.play(0.18f); break;
            case DANIO: golpe.play(0.5f); break;
            case EXPLOSION: explosion.play(0.35f); break;
            case MEJORA: disparo.play(0.35f, 1.6f, 0); break;
            case OLEADA: break;
            default: break;
        }
    }

    public boolean estaSilenciado() { return silenciado; }

    @Override
    public void dispose() {
        if (!cargado) return;
        musica.dispose();
        disparo.dispose();
        golpe.dispose();
        explosion.dispose();
        cargado = false;
    }
}
