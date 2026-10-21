package tecnostore.utilidades;

import java.util.regex.Pattern;

/**
 * Utilidades de validación reutilizables.
 * Métodos estáticos: no necesita estado, así que no se instancia.
 */
public final class Validador {

    /** Patrón de correo electrónico sencillo y razonable. */
    private static final Pattern PATRON_CORREO =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validador() {
        // Clase de utilidades: no instanciable.
    }

    /**
     * Verifica que un texto no sea nulo ni solo espacios en blanco.
     */
    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Verifica que un valor double sea estrictamente positivo (> 0).
     */
    public static boolean esPositivo(double valor) {
        return valor > 0;
    }

    /**
     * Verifica que un entero sea estrictamente positivo (> 0).
     */
    public static boolean esPositivo(int valor) {
        return valor > 0;
    }

    /**
     * Valida el formato de un correo electrónico.
     */
    public static boolean esCorreoValido(String correo) {
        if (!noVacio(correo)) {
            return false;
        }
        return PATRON_CORREO.matcher(correo.trim()).matches();
    }

    /**
     * Valida que la identificación sea un número o alfanumérico válido
     * (no vacío y sin caracteres especiales problemáticos).
     */
    public static boolean esIdentificacionValida(String identificacion) {
        if (!noVacio(identificacion)) {
            return false;
        }
        return identificacion.trim().matches("^[A-Za-z0-9\\-]{5,20}$");
    }
}
