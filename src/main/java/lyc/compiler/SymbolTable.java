package lyc.compiler;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTable {
    private static Map<String, String> symbols = new HashMap<>();

    public static void add(String id, String type) {
        symbols.put(id, type);
    }

    public static void save() {
        try {
            File outputDir = new File("target/output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter("target/output/symbol-table.txt")) {
                for (Map.Entry<String, String> entry : symbols.entrySet()) {
                    writer.write(entry.getKey() + " : " + entry.getValue() + "\n");
                }
                System.out.println("Symbol table saved successfully in target/output/symbol-table.txt");
            }
        } catch (IOException e) {
            System.err.println("Error saving symbol table: " + e.getMessage());
        }
    }
}