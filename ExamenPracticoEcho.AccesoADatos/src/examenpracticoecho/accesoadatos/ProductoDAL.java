package examenpracticoecho.accesoadatos;

import java.util.*;
import java.sql.*;
import examenpracticoecho.entidadesdenegocios.*;

public class ProductoDAL {
    
    static String obtenerCampos() {
        return "p.Id, p.IdCategoria, p.Nombre, p.Marca, p.Precio, p.Cantidad";
    }
    
    private static String obtenerSelect(Producto pProducto) {
        String sql;
        sql = "SELECT ";
        if (pProducto.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.SQLSERVER) {
             sql += "TOP " + pProducto.getTop_aux() + " ";
        }
        sql += (obtenerCampos() + " FROM Producto p");
        return sql;
    }
    
    private static String agregarOrderBy(Producto pProducto) {
        String sql = " ORDER BY p.Id DESC";
        if (pProducto.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.MYSQL) {
            sql += " LIMIT " + pProducto.getTop_aux() + " ";
        }
        return sql;
    }
    
    public static int crear(Producto pProducto) throws Exception {
        int result;
        String sql;
            try (Connection conn = ComunDB.obtenerConexion();) {
                sql = "INSERT INTO Producto(IdCategoria,Nombre,Marca,Precio,Cantidad) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                    ps.setInt(1, pProducto.getIdCategoria());
                    ps.setString(2, pProducto.getNombre());
                    ps.setString(3, pProducto.getMarca());
                    ps.setDouble(4, pProducto.getPrecio());
                    ps.setInt(5, pProducto.getCantidad());
                    result = ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    throw ex;
                }
                conn.close();
            }
            catch (SQLException ex) {
                throw ex;
            }
        return result;
    }
    
    public static int modificar(Producto pProducto) throws Exception {
        int result;
        String sql;
            try (Connection conn = ComunDB.obtenerConexion();) {                
                sql = "UPDATE Producto SET IdCategoria=?, Nombre=?, Marca=?, Precio=?, Cantidad=? WHERE Id=?";
                try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                    ps.setInt(1, pProducto.getIdCategoria());
                    ps.setString(2, pProducto.getNombre());
                    ps.setString(3, pProducto.getMarca());
                    ps.setDouble(4, pProducto.getPrecio());
                    ps.setInt(5, pProducto.getCantidad());
                    ps.setInt(6, pProducto.getId());
                    result = ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    throw ex;
                }
                conn.close();
            } 
            catch (SQLException ex) {
                throw ex;
            }
        return result;
    }
    
    public static int eliminar(Producto pProducto) throws Exception {
        int result;
        String sql;
        try (Connection conn = ComunDB.obtenerConexion();) { 
            sql = "DELETE FROM Producto WHERE Id=?"; 
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setInt(1, pProducto.getId());
                result = ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        return result;
    }
    
    static int asignarDatosResultSet(Producto pProducto, ResultSet pResultSet, int pIndex) throws Exception {
        pIndex++;
        pProducto.setId(pResultSet.getInt(pIndex)); 
        pIndex++;
        pProducto.setIdCategoria(pResultSet.getInt(pIndex)); 
        pIndex++;
        pProducto.setNombre(pResultSet.getString(pIndex)); 
        pIndex++;
        pProducto.setMarca(pResultSet.getString(pIndex)); 
        pIndex++;
        pProducto.setPrecio(pResultSet.getDouble(pIndex));
        pIndex++;
        pProducto.setCantidad(pResultSet.getInt(pIndex));
        return pIndex;
    }
    
    private static void obtenerDatos(PreparedStatement pPS, ArrayList<Producto> pProductos) throws Exception {
        try (ResultSet resultSet = ComunDB.obtenerResultSet(pPS);) { 
            while (resultSet.next()) {
                Producto producto = new Producto();
                asignarDatosResultSet(producto, resultSet, 0);
                pProductos.add(producto);
            }
            resultSet.close();
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    private static void obtenerDatosIncluirCategoria(PreparedStatement pPS, ArrayList<Producto> pProductos) throws Exception {
        try (ResultSet resultSet = ComunDB.obtenerResultSet(pPS);) {
            HashMap<Integer, Categoria> categoriaMap = new HashMap(); 
            while (resultSet.next()) {
                Producto producto = new Producto();
                int index = asignarDatosResultSet(producto, resultSet, 0);
                if (categoriaMap.containsKey(producto.getIdCategoria()) == false) {
                    Categoria categoria = new Categoria();
                    CategoriaDAL.asignarDatosResultSet(categoria, resultSet, index);
                    categoriaMap.put(categoria.getId(), categoria); 
                    producto.setCategoria(categoria); 
                } else {
                    producto.setCategoria(categoriaMap.get(producto.getIdCategoria())); 
                }
                pProductos.add(producto); 
            }
            resultSet.close();
        } catch (SQLException ex) {
            throw ex; 
        }
    }
    
    public static Producto obtenerPorId(Producto pProducto) throws Exception {
        Producto producto = new Producto();
        ArrayList<Producto> productos = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pProducto);
            sql += " WHERE p.Id=?";
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setInt(1, pProducto.getId());
                obtenerDatos(ps, productos);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        if (productos.size() > 0) {
            producto = productos.get(0);
        }
        return producto;
    }
    
    public static ArrayList<Producto> obtenerTodos() throws Exception {
        ArrayList<Producto> productos;
        productos = new ArrayList<>();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(new Producto()); 
            sql += agregarOrderBy(new Producto());
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                obtenerDatos(ps, productos);
                ps.close();
            } catch (SQLException ex) {
                throw ex; 
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        return productos;
    }
    
    static void querySelect(Producto pProducto, ComunDB.utilQuery pUtilQuery) throws SQLException {
        PreparedStatement statement = pUtilQuery.getStatement();
        if (pProducto.getId() > 0) {
            pUtilQuery.AgregarNumWhere(" p.Id=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pProducto.getId());
            }
        }

        if (pProducto.getIdCategoria() > 0) {
            pUtilQuery.AgregarNumWhere(" p.IdCategoria=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pProducto.getIdCategoria());
            }
        }
        
        if (pProducto.getNombre() != null && pProducto.getNombre().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" p.Nombre LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pProducto.getNombre() + "%");
            }
        }
        
        if (pProducto.getMarca() != null && pProducto.getMarca().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" p.Marca LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pProducto.getMarca() + "%");
            }
        }
        
        if (pProducto.getPrecio() > 0) {
            pUtilQuery.AgregarNumWhere(" p.Precio=? ");
            if (statement != null) { 
                statement.setDouble(pUtilQuery.getNumWhere(), pProducto.getPrecio()); 
            }
        }

        if (pProducto.getCantidad() > 0) {
            pUtilQuery.AgregarNumWhere(" p.Cantidad=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pProducto.getCantidad());
            }
        }
    }
    
    public static ArrayList<Producto> buscar(Producto pProducto) throws Exception {
        ArrayList<Producto> productos = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pProducto);
            ComunDB comundb = new ComunDB();
            ComunDB.utilQuery utilQuery = comundb.new utilQuery(sql, null, 0);
            querySelect(pProducto, utilQuery);
            sql = utilQuery.getSQL();
            sql += agregarOrderBy(pProducto);
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                utilQuery.setStatement(ps);
                utilQuery.setSQL(null);
                utilQuery.setNumWhere(0);
                querySelect(pProducto, utilQuery);
                obtenerDatos(ps, productos);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        } 
        catch (SQLException ex) {
            throw ex;
        }
        return productos;
    }

    public static ArrayList<Producto> buscarIncluirCategoria(Producto pProducto) throws Exception {
        ArrayList<Producto> productos = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = "SELECT ";
            if (pProducto.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.SQLSERVER) {
                sql += "TOP " + pProducto.getTop_aux() + " "; 
            }
            sql += obtenerCampos();
            sql += ",";
            sql += CategoriaDAL.obtenerCampos();
            sql += " FROM Producto p";
            sql += " JOIN Categoria c on (p.IdCategoria=c.Id)";
            ComunDB comundb = new ComunDB();
            ComunDB.utilQuery utilQuery = comundb.new utilQuery(sql, null, 0);
            querySelect(pProducto, utilQuery);
            sql = utilQuery.getSQL();
            sql += agregarOrderBy(pProducto);
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                utilQuery.setStatement(ps);
                utilQuery.setSQL(null);
                utilQuery.setNumWhere(0);
                querySelect(pProducto, utilQuery);
                obtenerDatosIncluirCategoria(ps, productos);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        } catch (SQLException ex) {
            throw ex;
        }
        return productos;
    }
  
}
