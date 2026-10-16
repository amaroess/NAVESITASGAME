package puppy.code.modelo;

import java.util.Random;
import org.junit.Test;
import puppy.code.armas.DisparoDoble;
import puppy.code.enemigos.Amenaza;
import puppy.code.enemigos.Jefe;
import static org.junit.Assert.*;

public class PartidaTest {
    @Test
    public void configuracionCreaLasAmenazasIndicadasIncluyendoJefe() {
        ConfiguracionOleada oleada = new ConfiguracionOleada.Builder().numero(3)
            .cantidadAsteroides(4).cantidadNavesEnemigas(2).incluirJefe(true).build();
        Partida partida = new Partida(new Random(1), oleada);
        assertEquals(7, partida.getAmenazas().size());
        assertTrue(partida.getAmenazas().get(0) instanceof Jefe);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void coleccionDeAmenazasNoSePuedeModificarDesdeFuera() {
        new Partida().getAmenazas().clear();
    }

    @Test
    public void siguienteOleadaConservaNaveVidasYArma() {
        ConfiguracionOleada oleada = new ConfiguracionOleada.Builder()
            .cantidadAsteroides(1).probabilidadPowerUp(0).build();
        Partida partida = new Partida(new Random(1), oleada);
        Nave nave = partida.getNave();
        nave.cambiarArma(new DisparoDoble());
        nave.recibirDanio(1);
        partida.getAmenazas().get(0).recibirDanio(1);
        partida.actualizar(0.01f, 0, 0, false);
        assertEquals(10, partida.getPuntaje());
        assertEquals(1, partida.getRonda());
        assertTrue(partida.getEsperaOleada() > 0);
        for (int i = 0; i < 370; i++) partida.actualizar(1f / 120, 0, 0, false);
        assertEquals(2, partida.getRonda());
        assertSame(nave, partida.getNave());
        assertEquals(2, nave.getVidas());
        assertEquals("DOBLE", nave.getNombreArma());
        assertEquals(10, partida.getPuntaje());
    }

    @Test
    public void heridaNoCongelaAmenazasNiProyectiles() {
        Partida partida = new Partida(new Random(2), new ConfiguracionOleada.Builder()
            .cantidadAsteroides(1).build());
        partida.getNave().recibirDanio(1);
        float y = partida.getAmenazas().get(0).getY();
        partida.actualizar(0.01f, 0, 0, true);
        assertNotEquals(y, partida.getAmenazas().get(0).getY(), 0.0001f);
        assertFalse(partida.getProyectiles().isEmpty());
    }

    @Test
    public void unSoloProyectilNoDestruyeDosAsteroidesSolapados() {
        Random azar = new Random() {
            @Override public float nextFloat() { return 0.5f; }
            @Override public boolean nextBoolean() { return false; }
        };
        Partida partida = new Partida(azar, new ConfiguracionOleada.Builder()
            .cantidadAsteroides(2).velocidadEnemigos(1).probabilidadPowerUp(0).build());
        partida.actualizar(1f / 120, 0, 0, true);
        for (int i = 0; i < 95; i++) partida.actualizar(1f / 120, 0, 0, false);
        assertEquals(10, partida.getPuntaje());
        assertEquals(1, partida.getAmenazas().size());
        assertTrue(partida.getProyectiles().isEmpty());
    }

    @Test
    public void segundaBajaOfreceMejoraYSePuntuaUnaSolaVez() {
        Partida partida = new Partida(new Random(1), new ConfiguracionOleada.Builder()
            .cantidadAsteroides(2).probabilidadPowerUp(0).build());
        for (Amenaza amenaza : partida.getAmenazas()) amenaza.recibirDanio(1);
        partida.actualizar(0.01f, 0, 0, false);
        assertEquals(20, partida.getPuntaje());
        assertEquals(1, partida.getMejoras().size());
        assertEquals(PowerUp.Tipo.DOBLE, partida.getMejoras().get(0).getTipo());
        partida.actualizar(0.01f, 0, 0, false);
        assertEquals(20, partida.getPuntaje());
    }

    @Test
    public void derrotaImpideAvanzarDeRonda() {
        Partida partida = new Partida();
        partida.getNave().recibirDanio(3);
        for (Amenaza amenaza : partida.getAmenazas()) amenaza.recibirDanio(100);
        partida.actualizar(10, 1, 1, true);
        assertTrue(partida.haTerminado());
        assertEquals(1, partida.getRonda());
        assertTrue(partida.getProyectiles().isEmpty());
    }
}
