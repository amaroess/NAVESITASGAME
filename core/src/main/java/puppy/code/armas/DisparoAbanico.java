package puppy.code.armas;

import java.util.Arrays;
import java.util.List;
import puppy.code.modelo.Proyectil;
import puppy.code.modelo.Proyectil.Equipo;

public final class DisparoAbanico implements EstrategiaDisparo {
    @Override
    public List<Proyectil> disparar(float centroX, float y) {
        return Arrays.asList(new Proyectil(centroX, y, -220, 610, Equipo.JUGADOR),
            new Proyectil(centroX, y, 0, 650, Equipo.JUGADOR),
            new Proyectil(centroX, y, 220, 610, Equipo.JUGADOR));
    }

    @Override public float getIntervalo() { return 0.3f; }
    @Override public String getNombre() { return "ABANICO"; }
}
