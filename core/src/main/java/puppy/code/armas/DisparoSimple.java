package puppy.code.armas;

import java.util.Collections;
import java.util.List;
import puppy.code.modelo.Proyectil;
import puppy.code.modelo.Proyectil.Equipo;

public final class DisparoSimple implements EstrategiaDisparo {
    @Override
    public List<Proyectil> disparar(float centroX, float y) {
        return Collections.singletonList(new Proyectil(centroX, y, 0, 650, Equipo.JUGADOR));
    }

    @Override public float getIntervalo() { return 0.22f; }
    @Override public String getNombre() { return "SIMPLE"; }
}
