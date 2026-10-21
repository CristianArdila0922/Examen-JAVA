package tecnostore.modelo;

import tecnostore.utilidades.Validador;

/**
 * Entidad Celular del catálogo de TecnoStore.
 * Aplica encapsulamiento: los atributos son privados y se validan
 * en los setters para mantener siempre un estado consistente.
 */
public class Celular {

    private int id;
    private String marca;
    private String modelo;
    private double precio;
    private int stock;
    private String sistemaOperativo;
    private CategoriaGama gama;

    public Celular() {
    }

    public Celular(int id, String marca, String modelo, double precio, int stock,
                   String sistemaOperativo, CategoriaGama gama) {
        this.id = id;
        setMarca(marca);
        setModelo(modelo);
        setPrecio(precio);
        setStock(stock);
        setSistemaOperativo(sistemaOperativo);
        setGama(gama);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        if (!Validador.noVacio(marca)) {
            throw new IllegalArgumentException("La marca del celular es obligatoria.");
        }
        this.marca = marca.trim();
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        if (!Validador.noVacio(modelo)) {
            throw new IllegalArgumentException("El modelo del celular es obligatorio.");
        }
        this.modelo = modelo.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (!Validador.esPositivo(precio)) {
            throw new IllegalArgumentException("El precio debe ser un valor positivo.");
        }
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        if (!Validador.noVacio(sistemaOperativo)) {
            throw new IllegalArgumentException("El sistema operativo es obligatorio.");
        }
        this.sistemaOperativo = sistemaOperativo.trim();
    }

    public CategoriaGama getGama() {
        return gama;
    }

    public void setGama(CategoriaGama gama) {
        if (gama == null) {
            throw new IllegalArgumentException("La gama del celular es obligatoria.");
        }
        this.gama = gama;
    }

    /**
     * Indica si el celular tiene stock bajo (menor a 5 unidades).
     */
    public boolean tieneStockBajo() {
        return stock < 5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Celular)) return false;
        Celular celular = (Celular) o;
        return id == celular.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format(
            "Celular{id=%d, marca='%s', modelo='%s', SO='%s', gama=%s, precio=%.2f, stock=%d}",
            id, marca, modelo, sistemaOperativo, gama.getEtiqueta(), precio, stock);
    }
}
