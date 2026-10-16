package puppy.code.enemigos;

import java.util.List;
import puppy.code.modelo.Proyectil;
import puppy.code.modelo.Proyectil.Equipo;

public final class Jefe extends Amenaza {
    private final float velocidad;
    private float direccion = 1;
    private float recarga = 1.5f;

    public Jefe(float x, float y, float velocidad, int vida) {
        super(x, y, 150, 105, vida, 500);
        this.velocidad = velocidad;
    }

    @Override
    protected void actualizarMovimiento(float delta) { mover(direccion * velocidad * delta, 0); }

    @Override
    protected void comprobarLimites(float ancho, float alto) {
        if (getX() <= 0) direccion = 1;
        if (getX() + getAncho() >= ancho) direccion = -1;
        limitarAlMundo(ancho, alto);
    }

    @Override
    protected void actualizarComportamiento(float delta, List<Proyectil> disparos) {
        recarga -= delta;
        if (recarga <= 0) {
            for (int i = -2; i <= 2; i++) {
                disparos.add(new Proyectil(getCentroX(), getY() - 20, i * 85, -260, Equipo.ENEMIGO));
            }
            recarga += getVida() <= getVidaMaxima() / 2 ? 0.8f : 1.3f;
        }
    }
}
