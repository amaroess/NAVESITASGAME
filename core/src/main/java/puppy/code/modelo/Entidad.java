package puppy.code.modelo;

import com.badlogic.gdx.math.Rectangle;

/** Geometria compartida; el rectangulo mutable nunca sale de la entidad. */
public abstract class Entidad {
    private final Rectangle area;

    protected Entidad(float x, float y, float ancho, float alto) {
        area = new Rectangle(x, y, ancho, alto);
    }

    public final boolean colisionaCon(Entidad otra) {
        return area.overlaps(otra.area);
    }

    protected final void mover(float dx, float dy) {
        area.x += dx;
        area.y += dy;
    }

    protected final void posicionar(float x, float y) {
        area.setPosition(x, y);
    }

    protected final void limitarAlMundo(float ancho, float alto) {
        posicionar(Math.max(0, Math.min(getX(), ancho - getAncho())),
            Math.max(0, Math.min(getY(), alto - getAlto())));
    }

    public final float getX() { return area.x; }
    public final float getY() { return area.y; }
    public final float getAncho() { return area.width; }
    public final float getAlto() { return area.height; }
    public final float getCentroX() { return area.x + area.width / 2; }
    public final float getCentroY() { return area.y + area.height / 2; }
}
