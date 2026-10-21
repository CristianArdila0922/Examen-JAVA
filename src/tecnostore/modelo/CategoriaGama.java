package tecnostore.modelo;

/**
 * Enum que representa la gama de un celular.
 * Usar un enum garantiza que solo se acepten valores válidos
 * y evita strings "mágicos" dispersos por el código.
 */
public enum CategoriaGama {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja");

    private final String etiqueta;

    CategoriaGama(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Permite obtener la gama a partir de un texto, ignorando mayúsculas/minúsculas.
     * Lanza IllegalArgumentException si el texto no corresponde a una gama válida.
     */
    public static CategoriaGama desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("La gama no puede ser nula.");
        }
        try {
            return CategoriaGama.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Gama inválida: '" + texto + "'. Valores válidos: Alta, Media, Baja.");
        }
    }
}
