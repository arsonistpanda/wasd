import java.io.*;
import java.util.*;

public class PassTwo {

    private Map<String, Integer> symbolTable;
    private Map<String, String> opcodeTable;

    public PassTwo() {
        // Initialize opcode table
        opcodeTable = new HashMap<>();
        opcodeTable.put("LDA", "00");
        opcodeTable.put("ADD", "01");
        opcodeTable.put("STA", "02");
        opcodeTable.put("JMP", "03");
        opcodeTable.put("WORD", "04"); // for data storage
    }

    public void execute(String intermediateFile, String symbolTableFile, String outputFile) throws IOException {
        BufferedReader interReader = new BufferedReader(new FileReader(intermediateFile));
        BufferedReader symReader = new BufferedReader(new FileReader(symbolTableFile));
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));

        symbolTable = new HashMap<>();
        String line;

        // Read symbol table
        while ((line = symReader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            if (tokens.length >= 2) {
                symbolTable.put(tokens[0], Integer.parseInt(tokens[1], 16));
            }
        }

        // Process intermediate code and generate machine code
        while ((line = interReader.readLine()) != null) {
            // Split the line into tokens (opcode and operands)
            String[] tokens = line.split("\\s+");

            // Ensure there are enough tokens
            if (tokens.length < 2) {
                System.out.println("Skipping malformed line: " + line);
                continue;  // Skip malformed line and go to the next one
            }

            // Get the address, opcode, and operand (if any)
            int address = Integer.parseInt(tokens[0]);
            String opcode = tokens[1];
            String operand = tokens.length > 2 ? tokens[2] : "";

            // Process valid opcode
            if (opcodeTable.containsKey(opcode)) {
                String opCodeBinary = opcodeTable.get(opcode);
                String operandAddress = operand.endsWith(":") 
                        ? Integer.toHexString(symbolTable.get(operand.replace(":", ""))) 
                        : operand;
                outputWriter.write(Integer.toHexString(address) + " " + opCodeBinary + " " + operandAddress + "\n");
            }
            // Process .WORD directive
            else if (opcode.equals(".WORD")) {
                outputWriter.write(Integer.toHexString(address) + " " + operand + "\n");
            }
        }

        // Close all resources
        interReader.close();
        symReader.close();
        outputWriter.close();
    }

    public static void main(String[] args) {
        PassTwo passTwo = new PassTwo();
        try {
            // Pass 2: Generate final machine code
            passTwo.execute("intermediate.txt", "symbolTable.txt", "output.obj");
            System.out.println("Pass 2 completed. Final machine code generated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
