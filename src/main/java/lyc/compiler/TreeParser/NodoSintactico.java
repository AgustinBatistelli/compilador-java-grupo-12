package lyc.compiler.TreeParser;

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

    public void setIzquierdo(NodoSintactico izq) {
        this.izquierdo = izq;
    }

    public void setDerecho(NodoSintactico der) {
        this.derecho = der;
    }

    public static NodoSintactico createBalancedTreeIterative(List<String> list) {
        if (list.isEmpty()) return null;

        Queue<NodoSintactico> queue = new LinkedList<>();
        for (String s : list) {
            queue.add(new NodoSintactico(s));
        }

        while (queue.size() > 1) {
            NodoSintactico left = queue.poll();
            NodoSintactico right = queue.poll();

            NodoSintactico parent = new NodoSintactico(",");
            parent.setIzquierdo(left);
            parent.setDerecho(right);

            queue.add(parent);
        }

        return queue.poll();
    }

}
