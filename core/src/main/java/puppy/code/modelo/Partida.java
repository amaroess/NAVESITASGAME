package puppy.code.modelo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import puppy.code.enemigos.Amenaza;
import puppy.code.enemigos.Asteroide;
import puppy.code.enemigos.Jefe;
import puppy.code.enemigos.NaveEnemiga;
import puppy.code.modelo.Proyectil.Equipo;

/** Reglas y estado de una partida. No depende de ventanas, audio ni texturas. */
public final class Partida {
    public static final float ANCHO = 1200;
    public static final float ALTO = 720;
    public enum Evento { DISPARO, DANIO, EXPLOSION, MEJORA, OLEADA }

    private final Nave nave = new Nave(ANCHO / 2 - 24, 55);
    private final List<Amenaza> amenazas = new ArrayList<>();
    private final List<Proyectil> proyectiles = new ArrayList<>();
    private final List<PowerUp> mejoras = new ArrayList<>();
    private final List<Amenaza> vistaAmenazas = Collections.unmodifiableList(amenazas);
    private final List<Proyectil> vistaProyectiles = Collections.unmodifiableList(proyectiles);
    private final List<PowerUp> vistaMejoras = Collections.unmodifiableList(mejoras);
    private final Queue<Evento> eventos = new ArrayDeque<>();
    private final Random azar;
    private ConfiguracionOleada oleada;
    private int puntaje;
    private int bajas;
    private float esperaOleada = -1;
    private String mensaje = "";
    private float tiempoMensaje;

    public Partida() { this(new Random(), ConfiguracionOleada.paraRonda(1)); }

    public Partida(Random azar, ConfiguracionOleada configuracion) {
        this.azar = Objects.requireNonNull(azar);
        iniciarOleada(Objects.requireNonNull(configuracion));
    }

    public void actualizar(float delta, float direccionX, float direccionY, boolean disparando) {
        if (delta < 0 || !Float.isFinite(delta)) {
            throw new IllegalArgumentException("El tiempo debe ser finito y no negativo");
        }
        if (haTerminado() || delta == 0) return;
        tiempoMensaje = Math.max(0, tiempoMensaje - delta);
        nave.actualizar(delta, direccionX, direccionY, ANCHO, ALTO);
        if (disparando) {
            List<Proyectil> nuevos = nave.disparar();
            if (!nuevos.isEmpty()) {
                proyectiles.addAll(nuevos);
                eventos.add(Evento.DISPARO);
            }
        }
        for (Amenaza amenaza : amenazas) amenaza.actualizar(delta, ANCHO, ALTO, proyectiles);
        rebotarAsteroides();
        for (Proyectil proyectil : proyectiles) proyectil.actualizar(delta, ANCHO, ALTO);
        resolverImpactos();
        retirarAmenazasDestruidas();
        proyectiles.removeIf(Proyectil::estaDestruido);

        // La derrota tiene prioridad sobre recoger mejoras y avanzar de oleada.
        if (haTerminado()) return;
        actualizarMejoras(delta);
        if (amenazas.isEmpty()) {
            if (esperaOleada < 0) {
                esperaOleada = 3;
                proyectiles.clear();
            } else {
                esperaOleada -= delta;
                if (esperaOleada <= 0) iniciarOleada(ConfiguracionOleada.paraRonda(oleada.getNumero() + 1));
            }
        }
    }

    private void rebotarAsteroides() {
        for (int i = 0; i < amenazas.size(); i++) {
            if (!(amenazas.get(i) instanceof Asteroide) || amenazas.get(i).estaDestruido()) continue;
            Asteroide primero = (Asteroide) amenazas.get(i);
            for (int j = i + 1; j < amenazas.size(); j++) {
                if (amenazas.get(j) instanceof Asteroide && !amenazas.get(j).estaDestruido()) {
                    primero.rebotarCon((Asteroide) amenazas.get(j));
                }
            }
        }
    }

    private void resolverImpactos() {
        for (Proyectil proyectil : proyectiles) {
            if (proyectil.estaDestruido()) continue;
            if (proyectil.getEquipo() == Equipo.JUGADOR) {
                for (Amenaza amenaza : amenazas) {
                    if (!amenaza.estaDestruido() && proyectil.colisionaCon(amenaza)) {
                        amenaza.recibirDanio(1);
                        proyectil.destruir();
                        break; // Un proyectil solo puede impactar a un enemigo.
                    }
                }
            } else if (proyectil.colisionaCon(nave)) {
                if (nave.recibirDanio(1)) eventos.add(Evento.DANIO);
                proyectil.destruir();
            }
        }
        for (Amenaza amenaza : amenazas) {
            if (!amenaza.estaDestruido() && amenaza.colisionaCon(nave) && nave.recibirDanio(1)) {
                amenaza.recibirDanio(1);
                eventos.add(Evento.DANIO);
            }
        }
    }

    private void retirarAmenazasDestruidas() {
        Iterator<Amenaza> iterador = amenazas.iterator();
        while (iterador.hasNext()) {
            Amenaza amenaza = iterador.next();
            if (!amenaza.estaDestruido()) continue;
            puntaje += amenaza.getPuntos();
            bajas++;
            eventos.add(Evento.EXPLOSION);
            // La segunda baja garantiza una oportunidad de probar el disparo doble.
            if (bajas == 2 || azar.nextFloat() < oleada.getProbabilidadPowerUp()) {
                PowerUp.Tipo tipo = bajas == 2 ? PowerUp.Tipo.DOBLE
                    : PowerUp.Tipo.values()[azar.nextInt(PowerUp.Tipo.values().length)];
                mejoras.add(new PowerUp(amenaza.getCentroX(), Math.max(20, amenaza.getCentroY()), tipo));
            }
            iterador.remove();
        }
    }

    private void actualizarMejoras(float delta) {
        Iterator<PowerUp> iterador = mejoras.iterator();
        while (iterador.hasNext()) {
            PowerUp mejora = iterador.next();
            mejora.actualizar(delta);
            if (mejora.colisionaCon(nave)) {
                mejora.aplicarA(nave);
                eventos.add(Evento.MEJORA);
                switch (mejora.getTipo()) {
                    case VIDA: anunciar("VIDA RECUPERADA"); break;
                    case ESCUDO: anunciar("ESCUDO ACTIVADO - 6 SEGUNDOS"); break;
                    case DOBLE: anunciar("ARMA: DISPARO DOBLE"); break;
                    case ABANICO: anunciar("ARMA: DISPARO EN ABANICO"); break;
                    default: break;
                }
            }
            if (mejora.haExpirado()) iterador.remove();
        }
    }

    private void iniciarOleada(ConfiguracionOleada configuracion) {
        oleada = configuracion;
        amenazas.clear();
        proyectiles.clear();
        esperaOleada = -1;
        float velocidad = oleada.getVelocidadEnemigos();
        if (oleada.incluyeJefe()) {
            amenazas.add(new Jefe(ANCHO / 2 - 75, ALTO - 120, velocidad, 20 + oleada.getNumero() * 5));
        }
        for (int i = 0; i < oleada.getCantidadAsteroides(); i++) {
            float tamano = 34 + azar.nextFloat() * 24;
            amenazas.add(new Asteroide(azar.nextFloat() * (ANCHO - tamano),
                320 + azar.nextFloat() * 270, tamano,
                (azar.nextBoolean() ? 1 : -1) * velocidad * (0.55f + azar.nextFloat() * 0.45f),
                -velocidad * (0.5f + azar.nextFloat() * 0.4f)));
        }
        for (int i = 0; i < oleada.getCantidadNavesEnemigas(); i++) {
            float x = (i + 1) * ANCHO / (oleada.getCantidadNavesEnemigas() + 1) - 23;
            amenazas.add(new NaveEnemiga(x, ALTO - 50, velocidad,
                oleada.getResistenciaEnemigos(), i * 0.7f));
        }
        anunciar("OLEADA " + oleada.getNumero() + (oleada.incluyeJefe() ? " - JEFE EN CAMINO" : ""));
        eventos.add(Evento.OLEADA);
    }

    private void anunciar(String texto) { mensaje = texto; tiempoMensaje = 2.5f; }
    public Nave getNave() { return nave; }
    public List<Amenaza> getAmenazas() { return vistaAmenazas; }
    public List<Proyectil> getProyectiles() { return vistaProyectiles; }
    public List<PowerUp> getMejoras() { return vistaMejoras; }
    public int getPuntaje() { return puntaje; }
    public int getRonda() { return oleada.getNumero(); }
    public float getEsperaOleada() { return esperaOleada; }
    public String getMensaje() { return tiempoMensaje > 0 ? mensaje : ""; }
    public boolean haTerminado() { return nave.estaDestruido(); }
    public Evento siguienteEvento() { return eventos.poll(); }
}
