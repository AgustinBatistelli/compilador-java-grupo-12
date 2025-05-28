package lyc.compiler.files;

import lyc.compiler.TreeParser.NodoSintactico;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class IntermediateCodeGenerator implements FileGenerator {

    private NodoSintactico root;

    public void setRoot(NodoSintactico root) {
        this.root = root;
    }

    public void generate() {
        File outputDir = new File("target/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File intermediateCodeFile = new File("target/output/intermidiate-code.txt");

        try (FileWriter fileWriter = new FileWriter(intermediateCodeFile)) {
            generate(fileWriter);  // llama al método con FileWriter
        } catch (IOException e) {
            System.err.println("Error writing intermediate code: " + e.getMessage());
        }
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        if (root == null) {
            fileWriter.write("No intermediate code generated (null root node).\n");
            return;
        }

        fileWriter.write("=== Árbol de Código Intermedio ===\n");
        writeTree(root, fileWriter, 0);
        System.out.println("Intermediate code tree successfully written.");
    }

    private void writeTree(NodoSintactico node, FileWriter writer, int indent) throws IOException {
        if (node == null) return;

        writer.write("  ".repeat(indent) + node.getValor() + "\n");

        writeTree(node.getIzquierdo(), writer, indent + 1);
        writeTree(node.getDerecho(), writer, indent + 1);
    }
}
