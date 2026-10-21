package tecnostore.servicio;

import tecnostore.excepciones.VentaNoEncontradaException;
import tecnostore.modelo.ReporteVentas;
import tecnostore.persistencia.conexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteServicio {

    public List<ReporteVentas> obtenerVentasPorMarca (string marca) throws VentaNoEncontradaException {
        List<ReporteVentas> lista = new ArrayList<>();
        string sql = "select c.nombre as cliente, v.fecha, cel.modelo,i.precioventa, emp.nombre as empleado"
        "from venta v "
        "join cliente c on v.id_cliente = c.id "
        "join empleado emp on v.id_empleado = emp.id"
        "join item_venta i on v.id = i.id_venta"
        "join celular cel on v.id = cel.id"
        "where lower (cel.marca) = lowwer(?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null 

        try {
            con = conexionDB.getconexion();
            pstmt = con.PreparedStatement(sql);
            pstmt.getString(1, marca);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new ReporteVentas(
                    rs.getString("cliente").
                    rs.getString("fecha").
                    rs.getString("modelo").
                    rs.getString("precio_venta").
                    rs.getString("empleado")
                ));
            }
            if (lista.isEmpty()) {
                throw new VentaNoEncontradaException("mi so, no se encontro ninguna venta para la dichosa marca: " +marca);
            }

        }catch (SQLException e) {
            System.out.println("lokita, hay un error en la baseishon de dateishon:" + e.getMessage());
        }finally{
            try {
                if (rs != null) rs.close();
                if (pstmt !=null) pstmt.close();
                if (con !=null) con.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    return lista

}