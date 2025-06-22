package lyc.compiler.files;

import lyc.compiler.TreeParser.NodoSintactico;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator implements FileGenerator {
    private static final List<NodoSintactico> arboles = new ArrayList<>();

    public IntermediateCodeGenerator() {
    }

    public void addTree(NodoSintactico root) {
        if (root != null) {
            arboles.add(root);
        }
    }

    @Override
    public void generate() {
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        int count = 1;
        for (NodoSintactico root : arboles) {
            fileWriter.write("Árbol de sentencia " + count++ + ":\n");
            printTree(fileWriter, root, "", "Root");
            fileWriter.write("\n");
        }
    }

    private void printTree(FileWriter writer, NodoSintactico node, String prefix, String label) throws IOException {
        if (node == null) return;

        writer.write(prefix + label + " -> " + node.getValor() + "\n");

        printTree(writer, node.getIzquierdo(), prefix + "  ", "Izq");
        printTree(writer, node.getDerecho(), prefix + "  ", "Der");
    }

    public void generateAssembler() {
        List<String> instrucciones = new ArrayList<>();

        for (NodoSintactico root : arboles) {
            instrucciones.addAll(generateAssemblerCodeFromTree(root));
        }

        AsmCodeGenerator asmGenerator = new AsmCodeGenerator(instrucciones);
        asmGenerator.generate();
    }

    private List<String> generateAssemblerCodeFromTree(NodoSintactico nodo) {
        List<String> instrucciones = new ArrayList<>();

        if (nodo == null) return instrucciones;

        String op = nodo.getValor();

        switch (op) {
            case ":=":
                String id = nodo.getIzquierdo().getValor();
                String expr = evaluarExpresion(nodo.getDerecho());
                instrucciones.add("mov ax, " + expr);
                instrucciones.add("mov " + id + ", ax");
                break;

            case "+":
            case "-":
            case "*":
            case "/":
                instrucciones.add("; Expresión: " + evaluarExpresion(nodo));
                break;

            case "WRITE":
                String toPrint = nodo.getIzquierdo().getValor();
                instrucciones.add("; PRINT: " + toPrint);
                break;

            case "READ":
                String var = nodo.getIzquierdo().getValor();
                instrucciones.add("; READ valor en " + var);
                break;

            default:
                instrucciones.add("; Nodo no reconocido: " + op);
                break;
        }

        return instrucciones;
    }

    private String evaluarExpresion(NodoSintactico nodo) {
        if (nodo == null) return "";

        if (nodo.esHoja()) return nodo.getValor();

        String izq = evaluarExpresion(nodo.getIzquierdo());
        String der = evaluarExpresion(nodo.getDerecho());
        String op = nodo.getValor();

        return izq + " " + op + " " + der;
    }

}
