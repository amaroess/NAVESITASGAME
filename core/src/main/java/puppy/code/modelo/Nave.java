package puppy.code.modelo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import puppy.code.armas.DisparoSimple;
import puppy.code.armas.EstrategiaDisparo;

public final class Nave extends Entidad implements Danable {
    public static final int VIDAS_MAXIMAS = 5;
    private static final float VELOCIDAD = 340;
    private int vidas = 3;
    private float invulnerabilidad;
    private float escudo;
    private float recarga;
    private EstrategiaDisparo arma = new DisparoSimple();

    public Nave(float x, float y) {
        super(x, y, 48, 48);
    }

    public void actualizar(float delta, float direccionX, float direccionY, float ancho, float alto) {
        if (estaDestruido()) return;
        invulnerabilidad = Math.max(0, invulnerabilidad - delta);
        escudo = Math.max(0, escudo - delta);
        recarga = Math.max(0, recarga - delta);
        float longitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
        if (longitud > 1) {
            direccionX /= longitud;
            direccionY /= longitud;
        }
        mover(direccionX * VELOCIDAD * delta, direccionY * VELOCIDAD * delta);
        limitarAlMundo(ancho, alto);
    }

    public List<Proyectil> disparar() {
        if (estaDestruido() || recarga > 0) return Collections.emptyList();
        recarga = arma.getIntervalo();
        return arma.disparar(getCentroX(), getY() + getAlto());
    }

    public void cambiarArma(EstrategiaDisparo nuevaArma) {
        arma = Objects.requireNonNull(nuevaArma, "El arma es obligatoria");
    }

    @Override
    public boolean recibirDanio(int cantidad) {
        if (cantidad <= 0 || estaDestruido() || invulnerabilidad > 0 || escudo > 0) return false;
        vidas = Math.max(0, vidas - cantidad);
        invulnerabilidad = 1.4f;
        return true;
    }

    public void recuperarVida() {
        if (!estaDestruido()) vidas = Math.min(VIDAS_MAXIMAS, vidas + 1);
    }

    public void activarEscudo(float segundos) {
        if (segundos <= 0 || !Float.isFinite(segundos)) {
            throw new IllegalArgumentException("La duracion debe ser positiva y finita");
        }
        escudo = Math.max(escudo, segundos);
    }

    @Override public boolean estaDestruido() { return vidas == 0; }
    public int getVidas() { return vidas; }
    public float getEscudoRestante() { return escudo; }
    public boolean estaHerida() { return invulnerabilidad > 0; }
    public String getNombreArma() { return arma.getNombre(); }
}
