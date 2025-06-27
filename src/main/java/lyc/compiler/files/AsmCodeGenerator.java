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
        fileWriter.write("include macros2.asm\n");
        fileWriter.write("include number.asm\n");
        fileWriter.write(".model LARGE\n");
        fileWriter.write(".386\n");
        fileWriter.write(".stack 200h\n");
        variables.addAll(SymbolTableGenerator.generarSeccionData());
        for (String variable : variables)
        {
            fileWriter.write(variable + "\n");
        }
        fileWriter.write( "    @cant dd ?"+ "\n");
        fileWriter.write( "    @suma dd ?"+ "\n");
        fileWriter.write( "    @multi dd ?"+ "\n");
        fileWriter.write( "    _cte0 dd 0.0"+ "\n");
        fileWriter.write(".code\n");
        fileWriter.write("start:\n");
        generateAssembler();
        fileWriter.write( "    mov ax,@data"+ "\n");
        fileWriter.write( "    mov ds,ax"+ "\n");
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
            case "=":
                generarAsignacionSimple(nodo);
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
            case "S":
                recorrerYGenerarAssembler(nodo.getIzquierdo());
                recorrerYGenerarAssembler(nodo.getDerecho());
                break;

            case "NEGATIVE_CALCULATION":
                generarNegativeCalculation(nodo);
                break;
        }
    }

    private void generarNegativeCalculation(NodoSintactico nodo) {
        recorrerYGenerarAssembler(nodo.getIzquierdo());
        recorrerYGenerarAssembler(nodo.getDerecho());
    }

    private void generarWrite(NodoSintactico nodo) {
        NodoSintactico arg = nodo.getIzquierdo();
        if (arg == null) return;
        String valor = arg.getValor();
        if (SymbolTableGenerator.tablaStrings.get(valor) != null) {
            instruccionesAssembler.add("mov ah, 09h");
            instruccionesAssembler.add("mov dx, offset " + SymbolTableGenerator.tablaStrings.get(valor));
            instruccionesAssembler.add("int 21h");
        } else {
            instruccionesAssembler.add("DisplayFloat "+valor +", 2");
        }

        instruccionesAssembler.add(""); // Línea vacía para legibilidad
    }

    public void generarInstrucciones(NodoSintactico nodo) {
        NodoSintactico lhs = nodo.getIzquierdo();
        NodoSintactico rhs = nodo.getDerecho();
        if (lhs == null || rhs == null) return;
        generarExpresion(rhs);
        instruccionesAssembler.add("FSTP " + lhs.getValor());
    }

    private void generarAsignacionSimple(NodoSintactico nodo) {
        String lhs = nodo.getIzquierdo().getValor();
        String rhs = nodo.getDerecho().getValor();
        if (esNumero(rhs)) {
            instruccionesAssembler.add("mov ax, " + rhs);
        } else {
            instruccionesAssembler.add("mov ax, [" + rhs + "]");
        }
        instruccionesAssembler.add("mov [" + lhs + "], ax");
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
            case "%%":
                generarExpresion(nodo.getIzquierdo());
                generarExpresion(nodo.getDerecho());
                instruccionesAssembler.add("FPREM");
                break;

            default:
                    instruccionesAssembler.add("FLD " + (esNumero(etiqueta) ? (etiqueta.contains(".")?"_ctef" + etiqueta.substring(0, etiqueta.indexOf(".")):"_cte" + etiqueta) : etiqueta));
        }
    }
    private void generarWhile(NodoSintactico nodo) {
        NodoSintactico condicion = nodo.getIzquierdo();
        NodoSintactico cuerpo = nodo.getDerecho();

        String etiquetaInicio = "WHILE_START" + (++whileCounter);
        String etiquetaFin = "WHILE_END" + (whileCounter);

        instruccionesAssembler.add(etiquetaInicio + ":");

        if (condicion == null) {
            instruccionesAssembler.add("; condición nula en WHILE");
            instruccionesAssembler.add("JMP " + etiquetaFin);
            instruccionesAssembler.add(etiquetaFin + ":");
            return;
        }

        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();
        String operador = condicion.getValor();

        String izqVal = (izq != null) ? izq.getValor() : "null";
        String derVal = (der != null) ? der.getValor() : "null";

        instruccionesAssembler.add("; WHILE " + izqVal + " " + operador + " " + derVal);
        if (izq != null)  instruccionesAssembler.add("FLD " + (esNumero(izq.getValor()) ? (izq.getValor().contains(".")?"_ctef" + izq.getValor().substring(0, izq.getValor().indexOf(".")):"_cte" + izq.getValor()) : izq.getValor()));
        if (der != null)  instruccionesAssembler.add("FLD " + (esNumero(der.getValor()) ? (der.getValor().contains(".")?"_ctef" + der.getValor().substring(0, der.getValor().indexOf(".")):"_cte" + der.getValor()) : der.getValor()));

        instruccionesAssembler.add("FXCH");
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
            default: instruccionesAssembler.add("; operador desconocido o condición incompleta");
        }

        if (cuerpo != null) {
            if ("CUERPO".equals(cuerpo.getValor())) {
                recorrerYGenerarAssembler(cuerpo.getIzquierdo());
                if (cuerpo.getDerecho() != null)
                    recorrerYGenerarAssembler(cuerpo.getDerecho());
            } else {
                recorrerYGenerarAssembler(cuerpo);
            }
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
        NodoSintactico condicion = nodo.getIzquierdo();
        NodoSintactico cuerpo = nodo.getDerecho();

        String etiquetaElse = "ELSE" + (++ifCounter);
        String etiquetaFin = "ENDIF" + (ifCounter);

        generarCondicion(condicion, etiquetaElse);

        // THEN o CUERPO
        if (cuerpo != null && "CUERPO".equals(cuerpo.getValor())) {
            recorrerYGenerarAssembler(cuerpo.getIzquierdo()); // THEN
            instruccionesAssembler.add("JMP " + etiquetaFin);
            instruccionesAssembler.add(etiquetaElse + ":");
            if (cuerpo.getDerecho() != null) {
                recorrerYGenerarAssembler(cuerpo.getDerecho()); // ELSE
            }
        } else {
            recorrerYGenerarAssembler(cuerpo); // solo THEN
            instruccionesAssembler.add(etiquetaElse + ":");
        }

        instruccionesAssembler.add(etiquetaFin + ":");
    }

    private void generarCondicion(NodoSintactico condicion, String etiquetaElse) {
        if (condicion == null) return;

        String op = condicion.getValor();

        switch (op) {
            case "AND":
                String etiquetaIntermedia = "COND_AND_" + ifCounter;

                generarCondicion(condicion.getIzquierdo(), etiquetaElse); // si izquierda es falsa → ELSE
                generarCondicion(condicion.getDerecho(), etiquetaElse);   // si derecha es falsa → ELSE
                break;

            case "OR":
                String etiquetaSig = "COND_OR_" + ifCounter;

                generarCondicion(condicion.getIzquierdo(), etiquetaSig); // si izquierda es verdadera → salto al cuerpo
                generarCondicion(condicion.getDerecho(), etiquetaElse);  // si derecha es falsa → ELSE
                instruccionesAssembler.add(etiquetaSig + ":");
                break;

            case "NOT":
                generarCondicionNot(condicion.getIzquierdo(), etiquetaElse);
                break;

            default:
                generarComparacion(condicion, etiquetaElse, false);
                break;
        }
    }

    private void generarCondicionNot(NodoSintactico condicion, String etiquetaElse) {
        if (condicion == null) return;

        String op = condicion.getValor();
        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();

        instruccionesAssembler.add("; NOT " + op + " " + (izq != null ? izq.getValor() : "") + " " + (der != null ? der.getValor() : ""));

        if (izq != null && der != null) {
            instruccionesAssembler.add("FLD " + izq.getValor());
            instruccionesAssembler.add("FLD " + der.getValor());
            instruccionesAssembler.add("FXCH");
            instruccionesAssembler.add("FCOMPP");
            instruccionesAssembler.add("FSTSW AX");
            instruccionesAssembler.add("SAHF");
        }

        // INVIERTE la lógica de salto
        switch (op) {
            case ">": instruccionesAssembler.add("JA " + etiquetaElse); break;
            case "<": instruccionesAssembler.add("JB " + etiquetaElse); break;
            case "==": instruccionesAssembler.add("JE " + etiquetaElse); break;
            case "!=": instruccionesAssembler.add("JNE " + etiquetaElse); break;
            case ">=": instruccionesAssembler.add("JAE " + etiquetaElse); break;
            case "<=": instruccionesAssembler.add("JBE " + etiquetaElse); break;
            default: instruccionesAssembler.add("; operador desconocido NOT: " + op);
        }
    }

    private void generarComparacion(NodoSintactico condicion, String etiquetaElse, boolean esNot) {
        if (condicion == null) return;

        String op = condicion.getValor();
        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();

        instruccionesAssembler.add("; IF " + op + " " + (izq != null ? izq.getValor() : "") + " " + (der != null ? der.getValor() : ""));

        if (izq != null && der != null) {
            generarExpresion(izq);
            generarExpresion(der);
            instruccionesAssembler.add("FXCH");
            instruccionesAssembler.add("FCOMPP");
            instruccionesAssembler.add("FSTSW AX");
            instruccionesAssembler.add("SAHF");
        }

        // Genera salto según operador, invirtiendo si es NOT
        switch (op) {
            case ">": instruccionesAssembler.add((esNot ? "JA" : "JBE") + " " + etiquetaElse); break;
            case "<": instruccionesAssembler.add((esNot ? "JB" : "JAE") + " " + etiquetaElse); break;
            case "==": instruccionesAssembler.add((esNot ? "JE" : "JNE") + " " + etiquetaElse); break;
            case "!=": instruccionesAssembler.add((esNot ? "JNE" : "JE") + " " + etiquetaElse); break;
            case ">=": instruccionesAssembler.add((esNot ? "JAE" : "JB") + " " + etiquetaElse); break;
            case "<=": instruccionesAssembler.add((esNot ? "JBE" : "JA") + " " + etiquetaElse); break;
            default: instruccionesAssembler.add("; operador desconocido: " + op);
        }
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
