package puppy.code.modelo;

import puppy.code.armas.DisparoAbanico;
import puppy.code.armas.DisparoDoble;

public final class PowerUp extends Entidad {
    public enum Tipo {
        VIDA("+"), ESCUDO("E"), DOBLE("D"), ABANICO("A");

        private final String etiqueta;
        Tipo(String etiqueta) { this.etiqueta = etiqueta; }
        public String getEtiqueta() { return etiqueta; }
    }

    private final Tipo tipo;
    private boolean consumido;

    public PowerUp(float centroX, float y, Tipo tipo) {
        super(centroX - 16, y, 32, 32);
        this.tipo = tipo;
    }

    public void actualizar(float delta) { mover(0, -95 * delta); }
    public boolean haExpirado() { return consumido || getY() + getAlto() < 0; }
    public Tipo getTipo() { return tipo; }

    public void aplicarA(Nave nave) {
        if (consumido || nave.estaDestruido()) return;
        switch (tipo) {
            case VIDA: nave.recuperarVida(); break;
            case ESCUDO: nave.activarEscudo(6); break;
            case DOBLE: nave.cambiarArma(new DisparoDoble()); break;
            case ABANICO: nave.cambiarArma(new DisparoAbanico()); break;
            default: throw new IllegalStateException("Power-up desconocido");
        }
        consumido = true;
    }
}
