package puppy.code.modelo;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfiguracionOleadaTest {
    @Test
    public void builderProduceConfiguracionesIndependientes() {
        ConfiguracionOleada.Builder builder = new ConfiguracionOleada.Builder().numero(2).cantidadAsteroides(4);
        ConfiguracionOleada primera = builder.build();
        ConfiguracionOleada segunda = builder.numero(3).cantidadAsteroides(8).incluirJefe(true).build();
        assertEquals(2, primera.getNumero());
        assertEquals(4, primera.getCantidadAsteroides());
        assertFalse(primera.incluyeJefe());
        assertEquals(3, segunda.getNumero());
        assertTrue(segunda.incluyeJefe());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaOleadaVacia() {
        new ConfiguracionOleada.Builder().cantidadAsteroides(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaCantidadNegativa() {
        new ConfiguracionOleada.Builder().cantidadNavesEnemigas(-1).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaProbabilidadInvalida() {
        new ConfiguracionOleada.Builder().probabilidadPowerUp(Float.NaN).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaVelocidadInfinita() {
        new ConfiguracionOleada.Builder().velocidadEnemigos(Float.POSITIVE_INFINITY).build();
    }

    @Test
    public void jefeCadaTresRondasYDificultadCreceConLimites() {
        assertFalse(ConfiguracionOleada.paraRonda(2).incluyeJefe());
        assertTrue(ConfiguracionOleada.paraRonda(3).incluyeJefe());
        assertTrue(ConfiguracionOleada.paraRonda(6).incluyeJefe());
        assertTrue(ConfiguracionOleada.paraRonda(2).getVelocidadEnemigos()
            > ConfiguracionOleada.paraRonda(1).getVelocidadEnemigos());
        assertEquals(30, ConfiguracionOleada.paraRonda(100).getCantidadAsteroides());
        assertEquals(240, ConfiguracionOleada.paraRonda(100).getVelocidadEnemigos(), 0);
    }
}
