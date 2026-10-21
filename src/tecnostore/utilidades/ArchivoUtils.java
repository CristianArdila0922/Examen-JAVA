package tecnostore.utilidades;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Utilidades para leer y escribir archivos de texto.
 * Usa NIO.2 con try-with-resources para garantizar el cierre correcto
 * de los recursos incluso ante excepciones.
 */
public final class ArchivoUtils {

    private ArchivoUtils() {
    }

    /**
     * Escribe (o sobrescribe) un archivo de texto con el contenido indicado.
     */
    public static void escribir(String ruta, String contenido) throws IOException {
        Path path = Paths.get(ruta);
        // Crea los directorios padres si no existen.
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(contenido);
        }
    }

    /**
     * Agrega líneas al final de un archivo (append).
     */
    public static void agregarLineas(String ruta, List<String> lineas) throws IOException {
        Path path = Paths.get(ruta);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /**
     * Lee un archivo completo a una lista de líneas.
     */
    public static List<String> leerLineas(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return reader.lines().toList();
        }
    }
}
