package tecnostore.modelo;

public class ReporteVentas {

    private string cliente;
    private string fecha;
    private string modeloCelular;
    private string precioVenta;
    private string empleado;

    public ReporteVentas(string cliente, string fecha, string modeloCelular, double precioVenta, string empleado) {
    this.cliente = cliente;
    this.fecha = fecha;
    this.modeloCelular = modeloCelular;
    this.precioVenta = precioVenta;
    this.empleado = empleado;
    }

    public string getCliente() {return cliente; }
    public string getFecha() {return fecha; }
    public string getModeloCelular() {return modeloCelular; }
    public double getPrecioVenta() {return precioVenta; }
    public string getEmpleado() {return empleado; } 
}
