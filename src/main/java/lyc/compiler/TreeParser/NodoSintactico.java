package lyc.compiler.TreeParser;

import java.util.List;

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

    public void setIzquierdo(NodoSintactico izq) {
        this.izquierdo = izq;
    }

    public void setDerecho(NodoSintactico der) {
        this.derecho = der;
    }

    public static NodoSintactico createBalancedTree(List<String> expList) {
            if (expList.size() == 1) {
                return NodoSintactico.crearHoja(expList.get(0));
            }

            NodoSintactico root = NodoSintactico.crearNodo(",", null, null);

            int mid = expList.size() / 2;
            root.setIzquierdo(createBalancedTree(expList.subList(0, mid)));
            root.setDerecho(createBalancedTree(expList.subList(mid, expList.size())));

            return root;
    }
}
