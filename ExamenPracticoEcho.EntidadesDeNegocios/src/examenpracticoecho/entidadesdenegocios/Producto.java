package examenpracticoecho.entidadesdenegocios;

import java.util.ArrayList;

public class Producto {
    private int id;
    private int idcategoria;
    private String nombre;
    private double preciounitario;
    private int top_aux;
    private Categoria categoria;

    public Producto() {
    }

    public Producto(int id, int idcategoria, String nombre, double preciounitario, int top_aux, Categoria categoria) {
        this.id = id;
        this.idcategoria = idcategoria;
        this.nombre = nombre;
        this.preciounitario = preciounitario;
        this.top_aux = top_aux;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPreciounitario() {
        return preciounitario;
    }

    public void setPreciounitario(double preciounitario) {
        this.preciounitario = preciounitario;
    }

    public int getTop_aux() {
        return top_aux;
    }

    public void setTop_aux(int top_aux) {
        this.top_aux = top_aux;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }   
    
}
