package puppy.code.modelo;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import puppy.code.armas.DisparoAbanico;
import puppy.code.enemigos.Asteroide;
import puppy.code.enemigos.Jefe;
import puppy.code.enemigos.NaveEnemiga;
import static org.junit.Assert.*;

public class CombateTest {
    @Test
    public void proyectilesEnAbanicoSeSeparanYExpiranFueraDelMundo() {
        List<Proyectil> disparos = new DisparoAbanico().disparar(600, 100);
        for (Proyectil disparo : disparos) disparo.actualizar(0.5f, 1200, 720);
        assertTrue(disparos.get(0).getX() < disparos.get(1).getX());
        assertTrue(disparos.get(2).getX() > disparos.get(1).getX());
        disparos.get(1).actualizar(1, 1200, 720);
        assertTrue(disparos.get(1).estaDestruido());
    }

    @Test
    public void asteroideRebotaYVuelveAlInterior() {
        Asteroide asteroide = new Asteroide(1155, 200, 40, 100, 0);
        asteroide.actualizar(0.1f, 1200, 720, new ArrayList<>());
        assertEquals(1160, asteroide.getX(), 0.01f);
        asteroide.actualizar(0.1f, 1200, 720, new ArrayList<>());
        assertEquals(1150, asteroide.getX(), 0.01f);
    }

    @Test
    public void asteroidesSolapadosSeSeparan() {
        Asteroide a = new Asteroide(100, 100, 40, 30, 30);
        Asteroide b = new Asteroide(110, 100, 40, -30, -30);
        a.rebotarCon(b);
        assertFalse(a.colisionaCon(b));
    }

    @Test
    public void naveEnemigaDisparaYRegresaCuandoSalePorAbajo() {
        NaveEnemiga enemigo = new NaveEnemiga(300, -50, 100, 2, 0);
        List<Proyectil> disparos = new ArrayList<>();
        enemigo.actualizar(1.4f, 1200, 720, disparos);
        assertEquals(674, enemigo.getY(), 0.01f);
        assertEquals(1, disparos.size());
        assertEquals(Proyectil.Equipo.ENEMIGO, disparos.get(0).getEquipo());
    }

    @Test
    public void jefeDisparaRafagasYDejaDeActuarAlMorir() {
        Jefe jefe = new Jefe(500, 550, 100, 20);
        List<Proyectil> disparos = new ArrayList<>();
        jefe.actualizar(1.6f, 1200, 720, disparos);
        assertEquals(5, disparos.size());
        jefe.recibirDanio(20);
        float x = jefe.getX();
        jefe.actualizar(2, 1200, 720, disparos);
        assertEquals(5, disparos.size());
        assertEquals(x, jefe.getX(), 0);
    }
}
