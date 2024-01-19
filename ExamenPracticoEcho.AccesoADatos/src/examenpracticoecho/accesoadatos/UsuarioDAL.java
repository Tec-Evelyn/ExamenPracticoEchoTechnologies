package examenpracticoecho.accesoadatos;

import java.util.*;
import java.sql.*;
import examenpracticoecho.entidadesdenegocios.*;

public class UsuarioDAL {
       public static String encriptarMD5(String txt) throws Exception {
        try {
            StringBuffer sb;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(txt.getBytes());
            sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw ex;
        }
    }
    
    static String obtenerCampos() {
        return "u.Id, u.IdRol, u.Nombre, u.Apellido, u.Login, u.Estatus, u.Telefono, u.Direccion";
    }
    
    private static String obtenerSelect(Usuario pUsuario) {
        String sql;
        sql = "SELECT ";
        if (pUsuario.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.SQLSERVER) {
             sql += "TOP " + pUsuario.getTop_aux() + " ";
        }
        sql += (obtenerCampos() + " FROM Usuario u");
        return sql;
    }
    
    private static String agregarOrderBy(Usuario pUsuario) {
        String sql = " ORDER BY u.Id DESC";
        if (pUsuario.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.MYSQL) {
            sql += " LIMIT " + pUsuario.getTop_aux() + " ";
        }
        return sql;
    }
    
    private static boolean existeLogin(Usuario pUsuario) throws Exception {
        boolean existe = false;
        ArrayList<Usuario> usuarios = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pUsuario);
            sql += " WHERE u.Id<>? AND u.Login=?";
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setInt(1, pUsuario.getId());
                ps.setString(2, pUsuario.getLogin());
                obtenerDatos(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        if (usuarios.size() > 0) {
            Usuario usuario = usuarios.get(0);
            if (usuario.getId() > 0 && usuario.getLogin().equals(pUsuario.getLogin())) {
                existe = true;
            }
        }
        return existe;
    }
    
    public static int crear(Usuario pUsuario) throws Exception {
        int result;
        String sql;
        boolean existe = existeLogin(pUsuario);
        if (existe == false) {
            try (Connection conn = ComunDB.obtenerConexion();) {
                sql = "INSERT INTO Usuario(IdRol,Nombre,Apellido,Login,Password,Estatus,Telefono,Direccion) VALUES(?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                    ps.setInt(1, pUsuario.getIdRol());
                    ps.setString(2, pUsuario.getNombre());
                    ps.setString(3, pUsuario.getApellido()); 
                    ps.setString(4, pUsuario.getLogin());
                    ps.setString(5, encriptarMD5(pUsuario.getPassword()));
                    ps.setByte(6, pUsuario.getEstatus());
                    ps.setString(7, pUsuario.getTelefono());
                    ps.setString(8, pUsuario.getDireccion());
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
        } else {
            result = 0;
            throw new RuntimeException("Login ya existe");
        }
        return result;
    }
    
    public static int modificar(Usuario pUsuario) throws Exception {
        int result;
        String sql;
        boolean existe = existeLogin(pUsuario);
        if (existe == false) {
            try (Connection conn = ComunDB.obtenerConexion();) {                
                sql = "UPDATE Usuario SET IdRol=?, Nombre=?, Apellido=?, Login=?, Estatus=?, Telefono=?, Direccion=? WHERE Id=?";
                try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                    ps.setInt(1, pUsuario.getIdRol());
                    ps.setString(2, pUsuario.getNombre());  
                    ps.setString(3, pUsuario.getApellido());
                    ps.setString(4, pUsuario.getLogin());
                    ps.setByte(5, pUsuario.getEstatus());
                    ps.setString(6, pUsuario.getTelefono());
                    ps.setString(7, pUsuario.getDireccion());
                    ps.setInt(8, pUsuario.getId());
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
        } else {
            result = 0;
            throw new RuntimeException("Login ya existe");
        }
        return result;
    }
    
    public static int eliminar(Usuario pUsuario) throws Exception {
        int result;
        String sql;
        try (Connection conn = ComunDB.obtenerConexion();) { 
            sql = "DELETE FROM Usuario WHERE Id=?"; 
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setInt(1, pUsuario.getId());
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
    
    static int asignarDatosResultSet(Usuario pUsuario, ResultSet pResultSet, int pIndex) throws Exception {
        pIndex++;
        pUsuario.setId(pResultSet.getInt(pIndex)); 
        pIndex++;
        pUsuario.setIdRol(pResultSet.getInt(pIndex)); 
        pIndex++;
        pUsuario.setNombre(pResultSet.getString(pIndex)); 
        pIndex++;
        pUsuario.setApellido(pResultSet.getString(pIndex)); 
        pIndex++;
        pUsuario.setLogin(pResultSet.getString(pIndex)); 
        pIndex++;
        pUsuario.setEstatus(pResultSet.getByte(pIndex)); 
        pIndex++;
        pUsuario.setTelefono(pResultSet.getString(pIndex)); 
        pIndex++;
        pUsuario.setDireccion(pResultSet.getString(pIndex)); 
        return pIndex;
    }
    
    private static void obtenerDatos(PreparedStatement pPS, ArrayList<Usuario> pUsuarios) throws Exception {
        try (ResultSet resultSet = ComunDB.obtenerResultSet(pPS);) { 
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                asignarDatosResultSet(usuario, resultSet, 0);
                pUsuarios.add(usuario);
            }
            resultSet.close();
        } catch (SQLException ex) {
            throw ex;
        }
    }
    
    private static void obtenerDatosIncluirRol(PreparedStatement pPS, ArrayList<Usuario> pUsuarios) throws Exception {
        try (ResultSet resultSet = ComunDB.obtenerResultSet(pPS);) {
            HashMap<Integer, Rol> rolMap = new HashMap(); 
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                int index = asignarDatosResultSet(usuario, resultSet, 0);
                if (rolMap.containsKey(usuario.getIdRol()) == false) {
                    Rol rol = new Rol();
                    RolDAL.asignarDatosResultSet(rol, resultSet, index);
                    rolMap.put(rol.getId(), rol); 
                    usuario.setRol(rol); 
                } else {
                    usuario.setRol(rolMap.get(usuario.getIdRol())); 
                }
                pUsuarios.add(usuario); 
            }
            resultSet.close();
        } catch (SQLException ex) {
            throw ex; 
        }
    }
    
    public static Usuario obtenerPorId(Usuario pUsuario) throws Exception {
        Usuario usuario = new Usuario();
        ArrayList<Usuario> usuarios = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pUsuario);
            sql += " WHERE u.Id=?";
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setInt(1, pUsuario.getId());
                obtenerDatos(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        if (usuarios.size() > 0) {
            usuario = usuarios.get(0);
        }
        return usuario;
    }
    
    public static ArrayList<Usuario> obtenerTodos() throws Exception {
        ArrayList<Usuario> usuarios;
        usuarios = new ArrayList<>();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(new Usuario()); 
            sql += agregarOrderBy(new Usuario());
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                obtenerDatos(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex; 
            }
            conn.close();
        }
        catch (SQLException ex) {
            throw ex;
        }
        return usuarios;
    }
    
    static void querySelect(Usuario pUsuario, ComunDB.utilQuery pUtilQuery) throws SQLException {
        PreparedStatement statement = pUtilQuery.getStatement();
        if (pUsuario.getId() > 0) {
            pUtilQuery.AgregarNumWhere(" u.Id=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pUsuario.getId());
            }
        }

        if (pUsuario.getIdRol() > 0) {
            pUtilQuery.AgregarNumWhere(" u.IdRol=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pUsuario.getIdRol());
            }
        }
        
        if (pUsuario.getNombre() != null && pUsuario.getNombre().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" u.Nombre LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pUsuario.getNombre() + "%");
            }
        }

        if (pUsuario.getApellido() != null && pUsuario.getApellido().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" u.Apellido LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pUsuario.getApellido() + "%");
            }
        }

        if (pUsuario.getLogin() != null && pUsuario.getLogin().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" u.Login=? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), pUsuario.getLogin());
            }
        }
        
        if (pUsuario.getEstatus() > 0) {
            pUtilQuery.AgregarNumWhere(" u.Estatus=? ");
            if (statement != null) {
                statement.setInt(pUtilQuery.getNumWhere(), pUsuario.getEstatus());
            }
        }

        if (pUsuario.getTelefono() != null && pUsuario.getTelefono().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" u.Telefono LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pUsuario.getTelefono() + "%");
            }
        }
        
        if (pUsuario.getDireccion() != null && pUsuario.getDireccion().trim().isEmpty() == false) {
            pUtilQuery.AgregarNumWhere(" u.Direccion LIKE ? ");
            if (statement != null) {
                statement.setString(pUtilQuery.getNumWhere(), "%" + pUsuario.getDireccion() + "%");
            }
        }        
    }
    
    public static ArrayList<Usuario> buscar(Usuario pUsuario) throws Exception {
        ArrayList<Usuario> usuarios = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pUsuario);
            ComunDB comundb = new ComunDB();
            ComunDB.utilQuery utilQuery = comundb.new utilQuery(sql, null, 0);
            querySelect(pUsuario, utilQuery);
            sql = utilQuery.getSQL();
            sql += agregarOrderBy(pUsuario);
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                utilQuery.setStatement(ps);
                utilQuery.setSQL(null);
                utilQuery.setNumWhere(0);
                querySelect(pUsuario, utilQuery);
                obtenerDatos(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        } 
        catch (SQLException ex) {
            throw ex;
        }
        return usuarios;
    }
    
    public static Usuario login(Usuario pUsuario) throws Exception {
        Usuario usuario = new Usuario();
        ArrayList<Usuario> usuarios = new ArrayList();
        String password = encriptarMD5(pUsuario.getPassword());
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = obtenerSelect(pUsuario);
            sql += " WHERE u.Login=? AND u.Password=? AND u.Estatus=?";
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                ps.setString(1, pUsuario.getLogin());
                ps.setString(2, password);
                ps.setByte(3, Usuario.EstatusUsuario.ACTIVO);
                obtenerDatos(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        } 
        catch (SQLException ex) {
            throw ex;
        }
        if (usuarios.size() > 0) {
            usuario = usuarios.get(0);
        }
        return usuario;
    }
    
    public static int cambiarPassword(Usuario pUsuario, String pPasswordAnt) throws Exception {
        int result;
        String sql;
        Usuario usuarioAnt = new Usuario();
        usuarioAnt.setLogin(pUsuario.getLogin());
        usuarioAnt.setPassword(pPasswordAnt);
        Usuario usuarioAut = login(usuarioAnt);

        if (usuarioAut.getId() > 0 && usuarioAut.getLogin().equals(pUsuario.getLogin())) {
            try (Connection conn = ComunDB.obtenerConexion();) {
                sql = "UPDATE Usuario SET Password=? WHERE Id=?";
                try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                    ps.setString(1, encriptarMD5(pUsuario.getPassword())); 
                    ps.setInt(2, pUsuario.getId());
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
        } else {
            result = 0;
            throw new RuntimeException("El password actual es incorrecto");
        }
        return result;
    }
    
    public static ArrayList<Usuario> buscarIncluirRol(Usuario pUsuario) throws Exception {
        ArrayList<Usuario> usuarios = new ArrayList();
        try (Connection conn = ComunDB.obtenerConexion();) {
            String sql = "SELECT ";
            if (pUsuario.getTop_aux() > 0 && ComunDB.TIPODB == ComunDB.TipoDB.SQLSERVER) {
                sql += "TOP " + pUsuario.getTop_aux() + " "; 
            }
            sql += obtenerCampos();
            sql += ",";
            sql += RolDAL.obtenerCampos();
            sql += " FROM Usuario u";
            sql += " JOIN Rol r on (u.IdRol=r.Id)";
            ComunDB comundb = new ComunDB();
            ComunDB.utilQuery utilQuery = comundb.new utilQuery(sql, null, 0);
            querySelect(pUsuario, utilQuery);
            sql = utilQuery.getSQL();
            sql += agregarOrderBy(pUsuario);
            try (PreparedStatement ps = ComunDB.createPreparedStatement(conn, sql);) {
                utilQuery.setStatement(ps);
                utilQuery.setSQL(null);
                utilQuery.setNumWhere(0);
                querySelect(pUsuario, utilQuery);
                obtenerDatosIncluirRol(ps, usuarios);
                ps.close();
            } catch (SQLException ex) {
                throw ex;
            }
            conn.close();
        } catch (SQLException ex) {
            throw ex;
        }
        return usuarios;
    }
  
}

