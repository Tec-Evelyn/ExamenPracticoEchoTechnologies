package examenpracticoecho.entidadesdenegocios;

import java.util.ArrayList;

public class Categoria {
    private int id;
    private String nombre;
    private int top_aux;
    private ArrayList<Producto> productos;

    public Categoria() {
    }

    public Categoria(int id, String nombre, int top_aux, ArrayList<Producto> productos) {
        this.id = id;
        this.nombre = nombre;
        this.top_aux = top_aux;
        this.productos = productos;
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
        this.nombre = nombre;
    }

    public int getTop_aux() {
        return top_aux;
    }

    public void setTop_aux(int top_aux) {
        this.top_aux = top_aux;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }
        
}
