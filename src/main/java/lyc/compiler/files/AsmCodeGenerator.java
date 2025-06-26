package lyc.compiler.files;

import lyc.compiler.TreeParser.NodoSintactico;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AsmCodeGenerator implements FileGenerator {

    private List<String> instruccionesAssembler=new ArrayList<>();
    private int ifCounter = 0;
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
        List <String> variables = new ArrayList<>();
        fileWriter.write(".model small\n");
        fileWriter.write(".stack 100h\n");
        fileWriter.write(".data\n");
        variables.addAll(SymbolTableGenerator.generarSeccionData());
        for (String variable : variables)
        {
            fileWriter.write(variable + "\n");
        }
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

            case "IF":
                generarIf(nodo);
                break;

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
                instruccionesAssembler.add("FLD " + (esNumero(etiqueta)?"_cte"+etiqueta:etiqueta));
                break;
        }
    }

    public boolean esNumero(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void generarIf(NodoSintactico nodo) {
        NodoSintactico condicion = nodo.getIzquierdo();  // comparación
        NodoSintactico cuerpo = nodo.getDerecho();       // puede ser CUERPO o directamente THEN

        String op = condicion.getValor(); // >, <, etc.
        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();

        String etiquetaElse = "ELSE" + (++ifCounter);
        String etiquetaFin = "ENDIF" + (ifCounter);

        instruccionesAssembler.add("; IF " + izq.getValor() + " " + op + " " + der.getValor());
        instruccionesAssembler.add("FLD " + izq.getValor());
        instruccionesAssembler.add("FLD " + der.getValor());
        instruccionesAssembler.add("FCOMPP");
        instruccionesAssembler.add("FSTSW AX");
        instruccionesAssembler.add("SAHF");

        switch (op) {
            case ">": instruccionesAssembler.add("JBE " + etiquetaElse); break;
            case "<": instruccionesAssembler.add("JAE " + etiquetaElse); break;
            case "==": instruccionesAssembler.add("JNE " + etiquetaElse); break;
            case "!=": instruccionesAssembler.add("JE " + etiquetaElse); break;
            case ">=": instruccionesAssembler.add("JB " + etiquetaElse); break;
            case "<=": instruccionesAssembler.add("JA " + etiquetaElse); break;
            default: instruccionesAssembler.add("; operador desconocido: " + op);
        }

        // THEN o CUERPO
        if (cuerpo.getValor().equals("CUERPO")) {
            recorrerYGenerarAssembler(cuerpo.getIzquierdo()); // THEN
            instruccionesAssembler.add("JMP " + etiquetaFin);
            instruccionesAssembler.add(etiquetaElse + ":");
            recorrerYGenerarAssembler(cuerpo.getDerecho()); // ELSE
        } else {
            recorrerYGenerarAssembler(cuerpo); // sólo THEN
            instruccionesAssembler.add(etiquetaElse + ":");
        }

        instruccionesAssembler.add(etiquetaFin + ":");
    }


}
