package puppy.code.modelo;

/** Producto inmutable del Builder; no contiene el estado del jugador. */
public final class ConfiguracionOleada {
    private final int numero;
    private final int cantidadAsteroides;
    private final int cantidadNavesEnemigas;
    private final float velocidadEnemigos;
    private final int resistenciaEnemigos;
    private final boolean incluirJefe;
    private final float probabilidadPowerUp;

    private ConfiguracionOleada(Builder builder) {
        numero = builder.numero;
        cantidadAsteroides = builder.cantidadAsteroides;
        cantidadNavesEnemigas = builder.cantidadNavesEnemigas;
        velocidadEnemigos = builder.velocidadEnemigos;
        resistenciaEnemigos = builder.resistenciaEnemigos;
        incluirJefe = builder.incluirJefe;
        probabilidadPowerUp = builder.probabilidadPowerUp;
    }

    public static ConfiguracionOleada paraRonda(int numero) {
        return new Builder().numero(numero)
            .cantidadAsteroides(Math.min(30, 10 + (numero - 1) * 2))
            .cantidadNavesEnemigas(Math.min(10, 1 + numero))
            .velocidadEnemigos(Math.min(240, 75 + numero * 12))
            .resistenciaEnemigos(Math.min(6, 2 + numero / 4))
            .incluirJefe(numero % 3 == 0)
            .probabilidadPowerUp(0.3f).build();
    }

    public int getNumero() { return numero; }
    public int getCantidadAsteroides() { return cantidadAsteroides; }
    public int getCantidadNavesEnemigas() { return cantidadNavesEnemigas; }
    public float getVelocidadEnemigos() { return velocidadEnemigos; }
    public int getResistenciaEnemigos() { return resistenciaEnemigos; }
    public boolean incluyeJefe() { return incluirJefe; }
    public float getProbabilidadPowerUp() { return probabilidadPowerUp; }

    public static final class Builder {
        private int numero = 1;
        private int cantidadAsteroides = 10;
        private int cantidadNavesEnemigas;
        private float velocidadEnemigos = 90;
        private int resistenciaEnemigos = 2;
        private boolean incluirJefe;
        private float probabilidadPowerUp = 0.3f;

        public Builder numero(int valor) { numero = valor; return this; }
        public Builder cantidadAsteroides(int valor) { cantidadAsteroides = valor; return this; }
        public Builder cantidadNavesEnemigas(int valor) { cantidadNavesEnemigas = valor; return this; }
        public Builder velocidadEnemigos(float valor) { velocidadEnemigos = valor; return this; }
        public Builder resistenciaEnemigos(int valor) { resistenciaEnemigos = valor; return this; }
        public Builder incluirJefe(boolean valor) { incluirJefe = valor; return this; }
        public Builder probabilidadPowerUp(float valor) { probabilidadPowerUp = valor; return this; }

        public ConfiguracionOleada build() {
            if (numero < 1 || cantidadAsteroides < 0 || cantidadNavesEnemigas < 0
                || resistenciaEnemigos < 1 || velocidadEnemigos <= 0
                || !Float.isFinite(velocidadEnemigos) || !Float.isFinite(probabilidadPowerUp)
                || probabilidadPowerUp < 0 || probabilidadPowerUp > 1) {
                throw new IllegalArgumentException("Configuracion de oleada invalida");
            }
            if (cantidadAsteroides == 0 && cantidadNavesEnemigas == 0 && !incluirJefe) {
                throw new IllegalArgumentException("La oleada necesita al menos una amenaza");
            }
            return new ConfiguracionOleada(this);
        }
    }
}
