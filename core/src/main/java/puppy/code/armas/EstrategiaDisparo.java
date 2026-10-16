package puppy.code.armas;

import java.util.List;
import puppy.code.modelo.Proyectil;

/** Strategy: la nave delega el disparo en un comportamiento intercambiable. */
public interface EstrategiaDisparo {
    List<Proyectil> disparar(float centroX, float y);
    float getIntervalo();
    String getNombre();
}
