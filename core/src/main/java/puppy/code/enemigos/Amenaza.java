package puppy.code.enemigos;

import java.util.List;
import puppy.code.modelo.Danable;
import puppy.code.modelo.Entidad;
import puppy.code.modelo.Proyectil;

public abstract class Amenaza extends Entidad implements Danable {
    private final int vidaMaxima;
    private final int puntos;
    private int vida;
    private float tiempoImpacto;

    protected Amenaza(float x, float y, float ancho, float alto, int vida, int puntos) {
        super(x, y, ancho, alto);
        if (vida < 1) throw new IllegalArgumentException("La vida debe ser positiva");
        this.vida = vida;
        vidaMaxima = vida;
        this.puntos = puntos;
    }

    /** Template Method: fija el orden; los subtipos especializan los pasos. */
    public final void actualizar(float delta, float ancho, float alto, List<Proyectil> disparos) {
        if (estaDestruido()) return;
        actualizarMovimiento(delta);
        comprobarLimites(ancho, alto);
        actualizarComportamiento(delta, disparos);
        tiempoImpacto = Math.max(0, tiempoImpacto - delta);
    }

    protected abstract void actualizarMovimiento(float delta);
    protected abstract void comprobarLimites(float ancho, float alto);

    /** Hook opcional: los asteroides no disparan. */
    protected void actualizarComportamiento(float delta, List<Proyectil> disparos) { }

    @Override
    public final boolean recibirDanio(int cantidad) {
        if (cantidad <= 0 || estaDestruido()) return false;
        vida = Math.max(0, vida - cantidad);
        tiempoImpacto = 0.09f;
        return true;
    }

    @Override public final boolean estaDestruido() { return vida == 0; }
    public final int getVida() { return vida; }
    public final int getVidaMaxima() { return vidaMaxima; }
    public final int getPuntos() { return puntos; }
    public final boolean estaImpactada() { return tiempoImpacto > 0; }
}
