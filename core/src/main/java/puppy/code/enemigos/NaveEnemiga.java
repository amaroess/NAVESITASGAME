package puppy.code.enemigos;

import java.util.List;
import puppy.code.modelo.Proyectil;
import puppy.code.modelo.Proyectil.Equipo;

public final class NaveEnemiga extends Amenaza {
    private final float velocidad;
    private final float fase;
    private float tiempo;
    private float recarga;

    public NaveEnemiga(float x, float y, float velocidad, int vida, float fase) {
        super(x, y, 46, 46, vida, 35);
        this.velocidad = velocidad;
        this.fase = fase;
        recarga = 1.3f + fase * 0.2f;
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        tiempo += delta;
        mover((float) Math.sin(tiempo * 2.2f + fase) * velocidad * delta,
            -velocidad * 0.48f * delta);
    }

    @Override
    protected void comprobarLimites(float ancho, float alto) {
        if (getY() + getAlto() < 0) posicionar(getX(), alto - getAlto());
        posicionar(Math.max(0, Math.min(getX(), ancho - getAncho())), getY());
    }

    @Override
    protected void actualizarComportamiento(float delta, List<Proyectil> disparos) {
        recarga -= delta;
        if (recarga <= 0) {
            disparos.add(new Proyectil(getCentroX(), getY() - 20, 0, -270, Equipo.ENEMIGO));
            recarga += 2.1f;
        }
    }
}
