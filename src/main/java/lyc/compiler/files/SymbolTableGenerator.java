package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTableGenerator implements FileGenerator {

    private static final Map<String, Constant> table = new LinkedHashMap<>();

    private static class Constant {
        String type;
        String value;
        int length;

        Constant(String type, String value, int length) {
            this.type = type;
            this.value = value;
            this.length = length;
        }
    }

    public static void addConstant(String identifier, String type, String value) {
        if (table.containsKey(identifier)) {
            System.out.println("Warning: Constant '" + identifier + "' is already declared.");
        } else {
            int length = calculateLength(type, value);
            table.put(identifier, new Constant(type, value, length));
            System.out.println("Added constant: " + identifier + " -> (Type: " + type + ", Value: " + value + ", Length: " + length + ")");
        }
    }

    public static void addVariable(String name, String type) {
        table.put(name, new Constant(type, "", 0));
    }

    public static boolean isVariableDefined(String name) {
        return table.containsKey(name);
    }

    public static String getTipo(String name) {
        Constant c = table.get(name);
        return (c != null) ? c.type : null;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("Identifier\tType\tValue\tLength\n");

        for (Map.Entry<String, Constant> entry : table.entrySet()) {
            String identifier = entry.getKey();
            Constant constant = entry.getValue();
            fileWriter.write(identifier + "\t|" + constant.type + "\t|" + constant.value + "\t|" + constant.length + "\n");
        }

        System.out.println("Symbol table successfully written.");
    }

    public void generate() {
        File outputDir = new File("target/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File symbolTableFile = new File("target/output/symbol-table.txt");

        try (FileWriter fileWriter = new FileWriter(symbolTableFile)) {
            generate(fileWriter);
        } catch (IOException e) {
            System.err.println("Error writing symbol table: " + e.getMessage());
        }
    }

    private static int calculateLength(String type, String value) {
        int length = 0;

        switch (type) {
            case "int":
                length = 4;
                break;
            case "float":
                length = 8;
                break;
            case "string":
                if (value != null) {
                    length = value.length();
                }
                break;
            default:
                System.out.println("Unknown type: " + type);
                break;
        }

        return length;
    }
}
