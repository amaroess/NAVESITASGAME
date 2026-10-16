package puppy.code.modelo;

/** Contrato de los objetos que pueden recibir impactos. */
public interface Danable {
    boolean recibirDanio(int cantidad);
    boolean estaDestruido();
}
