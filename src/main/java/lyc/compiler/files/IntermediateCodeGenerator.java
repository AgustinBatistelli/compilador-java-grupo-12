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
        // Constructor vacío como pediste
    }

    public void addTree(NodoSintactico root) {
        if (root != null) {
            arboles.add(root);
        }
    }

    @Override
    public void generate() {
        File outputDir = new File("target/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File intermidiateCodeFile = new File(outputDir, "intermidiate-code.txt");

        try (FileWriter fileWriter = new FileWriter(intermidiateCodeFile)) {
            generate(fileWriter);
        } catch (IOException e) {
            System.err.println("Error writing intermediate code: " + e.getMessage());
        }
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

        // Recorremos el hijo izquierdo primero
        printTree(writer, node.getIzquierdo(), prefix + "  ", "Izq");
        printTree(writer, node.getDerecho(), prefix + "  ", "Der");
    }

}
