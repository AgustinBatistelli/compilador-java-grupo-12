package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AsmCodeGenerator implements FileGenerator {

    private final List<String> instruccionesAssembler;

    public AsmCodeGenerator(List<String> instruccionesAssembler) {
        this.instruccionesAssembler = instruccionesAssembler;
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

        for (String instr : instruccionesAssembler) {
            fileWriter.write("    " + instr + "\n");
        }

        fileWriter.write("\n    mov ax, 4C00h\n");
        fileWriter.write("    int 21h\n");
        fileWriter.write("end start\n");
        System.out.println("Código assembler generado correctamente.");
    }
}
