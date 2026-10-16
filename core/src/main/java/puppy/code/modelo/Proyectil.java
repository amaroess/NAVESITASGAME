package puppy.code.modelo;

public final class Proyectil extends Entidad {
    public enum Equipo { JUGADOR, ENEMIGO }

    private final float velocidadX;
    private final float velocidadY;
    private final Equipo equipo;
    private boolean destruido;

    public Proyectil(float centroX, float y, float velocidadX, float velocidadY, Equipo equipo) {
        super(centroX - 4, y, 8, 20);
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.equipo = equipo;
    }

    public void actualizar(float delta, float ancho, float alto) {
        if (destruido) return;
        mover(velocidadX * delta, velocidadY * delta);
        if (getX() + getAncho() < 0 || getX() > ancho
            || getY() + getAlto() < 0 || getY() > alto) {
            destruir();
        }
    }

    public void destruir() { destruido = true; }
    public boolean estaDestruido() { return destruido; }
    public Equipo getEquipo() { return equipo; }
}
