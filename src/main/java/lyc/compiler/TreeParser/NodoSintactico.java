package lyc.compiler.TreeParser;

public class NodoSintactico {
    private String valor;
    private NodoSintactico izquierdo;
    private NodoSintactico derecho;

    private NodoSintactico(Object valor) {
        this.valor = (String) valor;
    }

    private NodoSintactico(Object valor, NodoSintactico izquierdo, NodoSintactico derecho) {
        this.valor = (String) valor;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    public static NodoSintactico crearHoja(Object valor) {
        return new NodoSintactico(valor);
    }

    public static NodoSintactico crearNodo(Object valor, NodoSintactico izq, NodoSintactico der) {
        return new NodoSintactico(valor, izq, der);
    }

    public String getValor() {
        return valor;
    }

    public NodoSintactico getIzquierdo() {
        return izquierdo;
    }

    public NodoSintactico getDerecho() {
        return derecho;
    }

    public NodoSintactico clonar() {
        NodoSintactico copiaIzq = (this.izquierdo != null) ? this.izquierdo.clonar() : null;
        NodoSintactico copiaDer = (this.derecho != null) ? this.derecho.clonar() : null;
        return new NodoSintactico(this.valor, copiaIzq, copiaDer);
    }
}
