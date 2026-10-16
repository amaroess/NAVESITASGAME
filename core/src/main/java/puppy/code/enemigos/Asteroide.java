package puppy.code.enemigos;

public final class Asteroide extends Amenaza {
    private float velocidadX;
    private float velocidadY;

    public Asteroide(float x, float y, float tamano, float velocidadX, float velocidadY) {
        super(x, y, tamano, tamano, 1, 10);
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        mover(velocidadX * delta, velocidadY * delta);
    }

    @Override
    protected void comprobarLimites(float ancho, float alto) {
        if (getX() < 0) velocidadX = Math.abs(velocidadX);
        if (getX() + getAncho() > ancho) velocidadX = -Math.abs(velocidadX);
        if (getY() < 0) velocidadY = Math.abs(velocidadY);
        if (getY() + getAlto() > alto) velocidadY = -Math.abs(velocidadY);
        limitarAlMundo(ancho, alto);
    }

    public void rebotarCon(Asteroide otro) {
        if (!colisionaCon(otro)) return;
        float solapamientoX = Math.min(getX() + getAncho(), otro.getX() + otro.getAncho())
            - Math.max(getX(), otro.getX());
        float solapamientoY = Math.min(getY() + getAlto(), otro.getY() + otro.getAlto())
            - Math.max(getY(), otro.getY());
        if (solapamientoX < solapamientoY) {
            float signo = getCentroX() < otro.getCentroX() ? -1 : 1;
            mover(signo * (solapamientoX / 2 + 0.01f), 0);
            otro.mover(-signo * (solapamientoX / 2 + 0.01f), 0);
            velocidadX = signo * Math.abs(velocidadX);
            otro.velocidadX = -signo * Math.abs(otro.velocidadX);
        } else {
            float signo = getCentroY() < otro.getCentroY() ? -1 : 1;
            mover(0, signo * (solapamientoY / 2 + 0.01f));
            otro.mover(0, -signo * (solapamientoY / 2 + 0.01f));
            velocidadY = signo * Math.abs(velocidadY);
            otro.velocidadY = -signo * Math.abs(otro.velocidadY);
        }
    }
}
