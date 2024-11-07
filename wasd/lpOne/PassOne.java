import java.io.*;
import java.util.*;

public class PassOne {

    private Map<String, Integer> symbolTable = new HashMap<>();
    private Map<String, String> opcodeTable;
    private int locationCounter;

    public PassOne() {
        opcodeTable = new HashMap<>();
        opcodeTable.put("LDA", "00");
        opcodeTable.put("ADD", "01");
        opcodeTable.put("STA", "02");
        opcodeTable.put("JMP", "03");
        opcodeTable.put("WORD", "04"); // for data storage
    }

    public void execute(String inputFile, String intermediateFile, String symbolTableFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter interWriter = new BufferedWriter(new FileWriter(intermediateFile));
        BufferedWriter symWriter = new BufferedWriter(new FileWriter(symbolTableFile));

        locationCounter = 0;
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length == 0) continue;

            String label = tokens[0];
            String opcode = tokens.length > 1 ? tokens[1] : "";
            String operand = tokens.length > 2 ? tokens[2] : "";

            if (opcode.equals("START")) {
                locationCounter = Integer.parseInt(operand, 16);
                interWriter.write(line + "\n");
                continue;
            }

            if (label.endsWith(":")) {
                symbolTable.put(label.replace(":", ""), locationCounter);
            }

            interWriter.write(locationCounter + " " + line + "\n");
            
            if (opcodeTable.containsKey(opcode)) {
                locationCounter += 4; // Assume each instruction is 4 bytes
            } else if (opcode.equals(".WORD")) {
                locationCounter += 4; // Data storage directive
            }
        }

        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            symWriter.write(entry.getKey() + " " + Integer.toHexString(entry.getValue()) + "\n");
        }

        reader.close();
        interWriter.close();
        symWriter.close();
    }

    public static void main(String[] args) {
        PassOne passOne = new PassOne();
        try {
            // Pass 1: Generate intermediate file and symbol table
            passOne.execute("input.asm", "intermediate.txt", "symbolTable.txt");
            System.out.println("Pass 1 completed. Intermediate file and symbol table generated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//javac *.java
//java PassOne
//java PassTwo