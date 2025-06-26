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
    private int stringCounter = 0;
    private int whileCounter = 0;
    private final List<String> constantesString = new ArrayList<>();
    private final List<String> etiquetasString = new ArrayList<>();
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

            case "WHILE":
                generarWhile(nodo);
                break;
            case "WRITE":
                generarWrite(nodo);
                break;
            case "REORDER":
                generarReorder(nodo);
                break;
           default:
                break;
        }
    }

    private void generarWrite(NodoSintactico nodo) {
        NodoSintactico arg = nodo.getIzquierdo();
        String valor = arg.getValor();
        if (SymbolTableGenerator.tablaStrings.get(valor)  != null)
        {
            instruccionesAssembler.add("mov ah, 09h");
            instruccionesAssembler.add("mov dx, offset " + SymbolTableGenerator.tablaStrings.get(valor));
            instruccionesAssembler.add("int 21h");
        }
        else {
            instruccionesAssembler.add("mov ax, [" + valor + "]");
            instruccionesAssembler.add("call printInt");
        }

        instruccionesAssembler.add(""); // Línea vacía para legibilidad
    }

    public void generarInstrucciones(NodoSintactico nodo) {
        if (!nodo.getValor().equals(":=")) return;

        NodoSintactico lhs = nodo.getIzquierdo();
        NodoSintactico rhs = nodo.getDerecho();

        generarExpresion(rhs);
        instruccionesAssembler.add("FSTP " + lhs.getValor());
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

    private void generarWhile(NodoSintactico nodo) {
        NodoSintactico condicion = nodo.getIzquierdo();
        NodoSintactico cuerpo = nodo.getDerecho();

        String etiquetaInicio = "WHILE_START" + (++whileCounter);
        String etiquetaFin = "WHILE_END" + (whileCounter);

        instruccionesAssembler.add(etiquetaInicio + ":");

        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();
        String operador = condicion.getValor();

        instruccionesAssembler.add("; WHILE " + izq.getValor() + " " + operador + " " + der.getValor());
        instruccionesAssembler.add("FLD " + izq.getValor());
        instruccionesAssembler.add("FLD " + der.getValor());
        instruccionesAssembler.add("FCOMPP");
        instruccionesAssembler.add("FSTSW AX");
        instruccionesAssembler.add("SAHF");

        switch (operador) {
            case ">": instruccionesAssembler.add("JBE " + etiquetaFin); break;
            case "<": instruccionesAssembler.add("JAE " + etiquetaFin); break;
            case "==": instruccionesAssembler.add("JNE " + etiquetaFin); break;
            case "!=": instruccionesAssembler.add("JE " + etiquetaFin); break;
            case ">=": instruccionesAssembler.add("JB " + etiquetaFin); break;
            case "<=": instruccionesAssembler.add("JA " + etiquetaFin); break;
            default: instruccionesAssembler.add("; operador desconocido: " + operador);
        }

        if (cuerpo.getValor().equals("CUERPO")) {
            recorrerYGenerarAssembler(cuerpo.getIzquierdo());
            if (cuerpo.getDerecho() != null)
                recorrerYGenerarAssembler(cuerpo.getDerecho());
        } else {
            recorrerYGenerarAssembler(cuerpo);
        }

        instruccionesAssembler.add("JMP " + etiquetaInicio);
        instruccionesAssembler.add(etiquetaFin + ":");
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

    private void generarReorder(NodoSintactico nodo) {
        List<NodoSintactico> expresiones = new ArrayList<>();
        extraerExpresionesNodo(nodo.getIzquierdo(), expresiones);

        for (NodoSintactico expr : expresiones) {
            generarExpresion(expr);
            instruccionesAssembler.add("");
        }

    }

    private void extraerExpresionesNodo(NodoSintactico nodo, List<NodoSintactico> lista) {
        if (nodo == null) return;

        if (".".equals(nodo.getValor()) || ",".equals(nodo.getValor())) {
            extraerExpresionesNodo(nodo.getIzquierdo(), lista);
            extraerExpresionesNodo(nodo.getDerecho(), lista);
        } else {
            lista.add(nodo);
        }
    }
}
