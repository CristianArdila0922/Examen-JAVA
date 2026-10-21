package tecnostore.modelo;

import tecnostore.utilidades.Validador;

/**
 * Entidad Cliente de TecnoStore.
 * El encapsulamiento valida correo no vacío y formato correcto.
 */
public class Cliente {

    private int id;
    private String nombre;
    private String identificacion;
    private String correo;
    private String telefono;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String identificacion, String correo, String telefono) {
        this.id = id;
        setNombre(nombre);
        setIdentificacion(identificacion);
        setCorreo(correo);
        setTelefono(telefono);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (!Validador.noVacio(nombre)) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        if (!Validador.noVacio(identificacion)) {
            throw new IllegalArgumentException("La identificación del cliente es obligatoria.");
        }
        this.identificacion = identificacion.trim();
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (!Validador.esCorreoValido(correo)) {
            throw new IllegalArgumentException("El correo '" + correo + "' no tiene un formato válido.");
        }
        this.correo = correo.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        // El teléfono es opcional; si llega vacío se guarda como cadena vacía.
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return identificacion != null && identificacion.equals(cliente.identificacion);
    }

    @Override
    public int hashCode() {
        return identificacion == null ? 0 : identificacion.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "Cliente{id=%d, nombre='%s', identificacion='%s', correo='%s', telefono='%s'}",
            id, nombre, identificacion, correo, telefono);
    }
}
