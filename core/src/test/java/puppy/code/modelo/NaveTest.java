package puppy.code.modelo;

import org.junit.Test;
import puppy.code.armas.DisparoAbanico;
import puppy.code.armas.DisparoDoble;
import static org.junit.Assert.*;

public class NaveTest {
    @Test
    public void movimientoNoDependeDeLosFps() {
        Nave a30 = new Nave(100, 100);
        Nave a144 = new Nave(100, 100);
        for (int i = 0; i < 30; i++) a30.actualizar(1f / 30, 1, 0, 1200, 720);
        for (int i = 0; i < 144; i++) a144.actualizar(1f / 144, 1, 0, 1200, 720);
        assertEquals(440, a30.getX(), 0.01f);
        assertEquals(a30.getX(), a144.getX(), 0.01f);
        a30.actualizar(0.5f, 0, 0, 1200, 720);
        assertEquals(440, a30.getX(), 0.01f);
    }

    @Test
    public void movimientoDiagonalNoAumentaVelocidad() {
        Nave nave = new Nave(100, 100);
        nave.actualizar(1, 1, 1, 1200, 720);
        double distancia = Math.hypot(nave.getX() - 100, nave.getY() - 100);
        assertEquals(340, distancia, 0.01);
    }

    @Test
    public void noSaleDelMundo() {
        Nave nave = new Nave(100, 100);
        nave.actualizar(100, 1, 1, 1200, 720);
        assertEquals(1152, nave.getX(), 0);
        assertEquals(672, nave.getY(), 0);
        nave.actualizar(100, -1, -1, 1200, 720);
        assertEquals(0, nave.getX(), 0);
        assertEquals(0, nave.getY(), 0);
    }

    @Test
    public void escudoBloqueaDanioYExpiraEnSegundos() {
        Nave nave = new Nave(100, 100);
        nave.activarEscudo(6);
        assertFalse(nave.recibirDanio(1));
        nave.actualizar(5, 0, 0, 1200, 720);
        assertFalse(nave.recibirDanio(1));
        nave.actualizar(1.01f, 0, 0, 1200, 720);
        assertTrue(nave.recibirDanio(1));
        assertEquals(2, nave.getVidas());
    }

    @Test
    public void invulnerabilidadEvitaPerderTodasLasVidasEnUnImpacto() {
        Nave nave = new Nave(100, 100);
        assertTrue(nave.recibirDanio(1));
        assertFalse(nave.recibirDanio(1));
        nave.actualizar(1.5f, 1, 0, 1200, 720);
        assertEquals(610, nave.getX(), 0.01f);
        assertTrue(nave.recibirDanio(1));
        assertEquals(1, nave.getVidas());
    }

    @Test
    public void derrotaEsInmediataYNoSePuedeRevivirConUnPowerUp() {
        Nave nave = new Nave(100, 100);
        nave.recibirDanio(20);
        assertTrue(nave.estaDestruido());
        assertEquals(0, nave.getVidas());
        nave.recuperarVida();
        assertEquals(0, nave.getVidas());
        assertTrue(nave.disparar().isEmpty());
    }

    @Test
    public void armasCambianEnEjecucionYRespetanRecarga() {
        Nave nave = new Nave(100, 100);
        assertEquals(1, nave.disparar().size());
        nave.cambiarArma(new DisparoDoble());
        assertTrue(nave.disparar().isEmpty());
        nave.actualizar(0.3f, 0, 0, 1200, 720);
        assertEquals(2, nave.disparar().size());
        nave.cambiarArma(new DisparoAbanico());
        nave.actualizar(0.3f, 0, 0, 1200, 720);
        assertEquals(3, nave.disparar().size());
    }

    @Test
    public void vidaTieneLimiteYMejoraSeConsumeUnaVez() {
        Nave nave = new Nave(100, 100);
        PowerUp vida = new PowerUp(100, 100, PowerUp.Tipo.VIDA);
        vida.aplicarA(nave);
        vida.aplicarA(nave);
        assertEquals(4, nave.getVidas());
        for (int i = 0; i < 10; i++) nave.recuperarVida();
        assertEquals(Nave.VIDAS_MAXIMAS, nave.getVidas());
        new PowerUp(100, 100, PowerUp.Tipo.ABANICO).aplicarA(nave);
        assertEquals("ABANICO", nave.getNombreArma());
        assertEquals(3, nave.disparar().size());
    }
}
