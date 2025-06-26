package lyc.compiler.files;

import lyc.compiler.TreeParser.NodoSintactico;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsmCodeGenerator implements FileGenerator {

    private List<String> instruccionesAssembler=new ArrayList<>();
    private static final List<NodoSintactico> arboles = new ArrayList<>();
    public void addTree(NodoSintactico root) {
        if (root != null) {
            arboles.add(root);
        }
    }

    public AsmCodeGenerator() {
    }

    @Override
    public void generate() {
        try (FileWriter writer = new FileWriter("final.asm")) {
            generate(writer);
        } catch (IOException e) {
            System.err.println("❌ Error al generar final.asm: " + e.getMessage());
        }
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(".model small\n");
        fileWriter.write(".stack 100h\n");
        fileWriter.write(".data\n");
        fileWriter.write("    ; ← Acá van tus variables y constantes\n\n");
        fileWriter.write(".code\n");
        fileWriter.write("start:\n");
        generateAssembler();
        for (String instr : instruccionesAssembler) {
            fileWriter.write("    " + instr + "\n");
        }

        fileWriter.write("\n    mov ax, 4C00h\n");
        fileWriter.write("    int 21h\n");
        fileWriter.write("end start\n");
        System.out.println("Código assembler generado correctamente.");
    }

    public void generateAssembler() {
        for (NodoSintactico root : arboles) {
            recorrerYGenerarAssembler(root);
        }
    }

    public void recorrerYGenerarAssembler(NodoSintactico nodo) {
        if (nodo == null) return;

        String etiqueta = nodo.getValor();

        switch (etiqueta) {
            case ";":
                recorrerYGenerarAssembler(nodo.getIzquierdo());
                recorrerYGenerarAssembler(nodo.getDerecho());
                break;

            case ":=":
                generarInstrucciones(nodo);
                break;

            // Podés agregar otros casos si querés procesar más cosas (como IF, WRITE, etc.)
            default:
                break;
        }
    }

    public void generarInstrucciones(NodoSintactico nodo) {
        if (!nodo.getValor().equals(":=")) return;

        NodoSintactico lhs = nodo.getIzquierdo(); // variable destino
        NodoSintactico rhs = nodo.getDerecho(); // expresión

        generarExpresion(rhs); // genera el cálculo
        instruccionesAssembler.add("FSTP " + lhs.getValor()); // guarda el resultado
    }

    private void generarExpresion(NodoSintactico nodo) {
        if (nodo == null) return;

        String etiqueta = nodo.getValor();

        switch (etiqueta) {
            case "+":
                generarExpresion(nodo.getIzquierdo());
                generarExpresion(nodo.getDerecho());
                instruccionesAssembler.add("FADD");
                break;
            case "-":
                generarExpresion(nodo.getIzquierdo());
                generarExpresion(nodo.getDerecho());
                instruccionesAssembler.add("FSUB");
                break;
            case "*":
                generarExpresion(nodo.getIzquierdo());
                generarExpresion(nodo.getDerecho());
                instruccionesAssembler.add("FMUL");
                break;
            case "/":
                generarExpresion(nodo.getIzquierdo());
                generarExpresion(nodo.getDerecho());
                instruccionesAssembler.add("FDIV");
                break;
            default:
                // Es un número o identificador
                instruccionesAssembler.add("FLD " + etiqueta);
                break;
        }
    }

}
