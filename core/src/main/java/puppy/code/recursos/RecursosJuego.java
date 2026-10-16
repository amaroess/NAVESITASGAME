package puppy.code.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/** Las texturas se cargan una vez y se liberan al cerrar la aplicacion. */
public final class RecursosJuego implements Disposable {
    private final AssetManager gestor = new AssetManager();

    public RecursosJuego() {
        for (String archivo : new String[] { "MainShip3.png", "Rocket2.png", "aGreyMedium4.png", "aGreyLarge.png" }) {
            gestor.load(archivo, Texture.class);
        }
        gestor.finishLoading();
        Gdx.app.log("RecursosJuego", "Texturas cargadas");
    }

    public Texture getNave() { return gestor.get("MainShip3.png", Texture.class); }
    public Texture getProyectil() { return gestor.get("Rocket2.png", Texture.class); }
    public Texture getAsteroide() { return gestor.get("aGreyMedium4.png", Texture.class); }
    public Texture getAsteroideGrande() { return gestor.get("aGreyLarge.png", Texture.class); }
    @Override public void dispose() { gestor.dispose(); }
}
