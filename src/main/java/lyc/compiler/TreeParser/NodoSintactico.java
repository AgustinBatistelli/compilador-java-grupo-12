package lyc.compiler.TreeParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    public boolean esHoja() {
        return this.izquierdo == null && this.derecho == null;
    }

    public static List<NodoSintactico> procesarListaExpresiones(NodoSintactico nodo) {
        List<NodoSintactico> lista = new ArrayList<>();
        while (nodo != null) {
            if (nodo.getValor().equals(",")) {
                lista.add(nodo.getIzquierdo());
                nodo = nodo.getDerecho();
            } else {
                lista.add(nodo);
                break;
            }
        }
        return lista;
    }

    public static String imprimirExpresion(NodoSintactico nodo) {
        if (nodo == null) return "";
        if (nodo.esHoja()) return nodo.getValor();

        String izq = imprimirExpresion(nodo.getIzquierdo());
        String der = imprimirExpresion(nodo.getDerecho());

        return "(" + izq + " " + nodo.getValor() + " " + der + ")";
    }
}
