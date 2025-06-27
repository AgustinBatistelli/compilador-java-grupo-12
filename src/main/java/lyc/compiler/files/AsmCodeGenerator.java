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
    private final List<String> constantesString = new ArrayList<>();
    private final List<String> etiquetasString = new ArrayList<>();
    private static final List<NodoSintactico> arboles = new ArrayList<>();

    public void addTree(NodoSintactico root) {
        if (root != null) {
            arboles.add(root);
        }
    }

    public AsmCodeGenerator() {}

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
        List<String> variables = new ArrayList<>();
        fileWriter.write(".model small\n");
        fileWriter.write(".stack 100h\n");
        fileWriter.write(".data\n");
        variables.addAll(SymbolTableGenerator.generarSeccionData());
        for (String variable : variables) {
            fileWriter.write(variable + "\n");
        }
        fileWriter.write( "    @cant dd ?"+ "\n");
        fileWriter.write( "    @suma dd ?"+ "\n");
        fileWriter.write( "    @multi dd ?"+ "\n");
        fileWriter.write( "    _cte0 dd 0"+ "\n");
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
            case "=":
                generarAsignacionSimple(nodo);
                break;
            case "IF":
                generarIf(nodo);
                break;
            case "WRITE":
                generarWrite(nodo);
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
        String valor = arg.getValor();
        if (SymbolTableGenerator.tablaStrings.get(valor) != null) {
            instruccionesAssembler.add("mov ah, 09h");
            instruccionesAssembler.add("mov dx, offset " + SymbolTableGenerator.tablaStrings.get(valor));
            instruccionesAssembler.add("int 21h");
        } else {
            instruccionesAssembler.add("mov ax, [" + valor + "]");
            instruccionesAssembler.add("call printInt");
        }
        instruccionesAssembler.add("");
    }

    public void generarInstrucciones(NodoSintactico nodo) {
        NodoSintactico lhs = nodo.getIzquierdo();
        NodoSintactico rhs = nodo.getDerecho();
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
            default:
                if(SymbolTableGenerator.tablaStrings.get(etiqueta) != null) {
                    instruccionesAssembler.add("FLD " + (esNumero(etiqueta) ? "_cte" + etiqueta : etiqueta));
                }
                else
                {
                    instruccionesAssembler.add("FLD " + etiqueta);
                }
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
        NodoSintactico condicion = nodo.getIzquierdo();
        NodoSintactico cuerpo = nodo.getDerecho();
        String op = condicion.getValor();
        NodoSintactico izq = condicion.getIzquierdo();
        NodoSintactico der = condicion.getDerecho();
        String etiquetaElse = "ELSE" + (++ifCounter);
        String etiquetaFin = "ENDIF" + (ifCounter);

        instruccionesAssembler.add("; IF " + izq.getValor() + " " + op + " " + der.getValor());

        if (op.equals("%%")) {
            instruccionesAssembler.add("MOV AX, [" + izq.getValor() + "]");
            instruccionesAssembler.add("MOV BL, " + der.getValor());
            instruccionesAssembler.add("DIV BL");
            instruccionesAssembler.add("CMP AH, 0");
            instruccionesAssembler.add("JNE " + etiquetaElse);
        } else {
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
        }

        if (cuerpo.getValor().equals("CUERPO")) {
            recorrerYGenerarAssembler(cuerpo.getIzquierdo());
            instruccionesAssembler.add("JMP " + etiquetaFin);
            instruccionesAssembler.add(etiquetaElse + ":");
            recorrerYGenerarAssembler(cuerpo.getDerecho());
        } else {
            recorrerYGenerarAssembler(cuerpo);
            instruccionesAssembler.add(etiquetaElse + ":");
        }

        instruccionesAssembler.add(etiquetaFin + ":");
    }
}
