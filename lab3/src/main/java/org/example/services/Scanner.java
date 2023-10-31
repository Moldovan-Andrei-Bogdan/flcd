package org.example.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.example.entity.SymbolTable;
import org.example.exception.ScannerException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
    private String program;

    private final List<String> tokens;

    private final List<String> reservedWords;

    private int index = 0;

    private int currentLine = 1;

    private SymbolTable symbolTable;

    private List<Pair<String, Pair<Integer, Integer>>> PIF;

    public Scanner() {
        this.symbolTable = new SymbolTable(47);
        this.PIF = new ArrayList<>();
        this.reservedWords = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.executeRead();
    }

    private void executeRead() {
        try {
            readTokens();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProgram(String program) {
        this.program = program;
    }

    private void readTokens() throws IOException {
        File file = new File("src/main/resources/token.in");

        BufferedReader br = Files.newBufferedReader(file.toPath());

        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "prog", "int", "str", "char", "read", "if", "else", "print", "do", "while", "arr", "const", "fun", "real" -> reservedWords.add(parts[0]);
                default -> tokens.add(parts[0]);
            }
        }
    }

    private void discardSpaces() {
        while (index < program.length() && Character.isWhitespace(program.charAt(index))) {
            if (program.charAt(index) == '\n') {
                currentLine++;
            }
            index++;
        }
    }

    private boolean treatStringConstant() {
        Pattern regexForStringConstant = Pattern.compile("^\"[a-zA-z0-9_ ?:*^+=.!]*\"");
        Pattern regexForInvalidSingleChar = Pattern.compile("^\"[^\"]\"");
        Pattern regexForInvalidOpenedString = Pattern.compile("^\"[^\"]");

        Matcher stringConstantMatcher = regexForStringConstant.matcher(program.substring(index));
        Matcher invalidSingleCharMatcher = regexForInvalidSingleChar.matcher(program.substring(index));
        Matcher invalidOpenedStringMatcher = regexForInvalidOpenedString.matcher(program.substring(index));

        if (!stringConstantMatcher.find()) {
            if (invalidSingleCharMatcher.find()) {
                throw new ScannerException("Invalid string constant at line " + currentLine);
            }

            if (invalidOpenedStringMatcher.find()) {
                throw new ScannerException("Missing \" at line " + currentLine);
            }

            return false;
        }

        String stringConstant = stringConstantMatcher.group(0);

        index += stringConstant.length();

        Pair<Integer, Integer> position;

        try {
            position = symbolTable.addStringConstant(stringConstant);
        } catch (Exception e) {
            position = symbolTable.getPositionStringConstant(stringConstant);
        }
        PIF.add(new ImmutablePair<>("str const", position));

        return true;
    }

    private boolean treatIntConstant(){
        Pattern regexForIntConstant = Pattern.compile("^([+-]?[1-9][0-9]*|0)");
        Pattern regexForInvalidIntConstant = Pattern.compile("^([+-]?[1-9][0-9]*|0)[a-zA-z_]");

        Matcher intConstantMatcher = regexForIntConstant.matcher(program.substring(index));
        Matcher InvalidIntConstantMatcher = regexForInvalidIntConstant.matcher(program.substring(index));

        if (!intConstantMatcher.find()) {
            return false;
        }

        if (InvalidIntConstantMatcher.find()) {
            return false;
        }

        String intConstant = intConstantMatcher.group(1);

        index += intConstant.length();

        Pair<Integer, Integer> position;

        try {
            position = symbolTable.addIntConstant(Integer.parseInt(intConstant));
        } catch (Exception e) {
            position = symbolTable.getPositionIntConstant(Integer.parseInt(intConstant));
        }
        PIF.add(new ImmutablePair<>("int const", position));

        return true;
    }

    private boolean checkIfValid(String possibleIdentifier, String programSubstring) {
        Pattern regexForVariable = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]* \\((int|char|str|real)\\)");
        Matcher matcher = regexForVariable.matcher(programSubstring);

        if (reservedWords.contains(possibleIdentifier)) {
            return false;
        }

        if (matcher.find()) {
            return true;
        }

        return symbolTable.hasIdentifier(possibleIdentifier);
    }

    private boolean treatIdentifier() {
        var regexForIdentifier = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)");

        var matcher = regexForIdentifier.matcher(program.substring(index));

        if (!matcher.find()) {
            return false;
        }

        var identifier = matcher.group(1);

        if (!checkIfValid(identifier, program.substring(index))) {
            return false;
        }

        index += identifier.length();
        Pair<Integer, Integer> position;
        try {
            position = symbolTable.addIdentifier(identifier);
        } catch (Exception e) {
            position = symbolTable.getPositionIdentifier(identifier);
        }
        PIF.add(new ImmutablePair<>("identifier", position));

        return true;
    }

    private boolean treatFromTokenList() {
        String possibleToken = program.substring(index).split(" ")[0];

        for (var reservedToken: reservedWords) {
            if (possibleToken.startsWith(reservedToken)) {
                var regex = "^" + "[a-zA-Z0-9_]*" + reservedToken + "[a-zA-Z0-9_]+";

                if (Pattern.compile(regex).matcher(possibleToken).find()) {
                    return false;
                }

                index += reservedToken.length();
                PIF.add(new ImmutablePair<>(reservedToken, new ImmutablePair<>(-1, -1)));

                return true;
            }
        }

        for (var token : tokens) {
            if (Objects.equals(token, possibleToken)) {
                index += token.length();
                PIF.add(new ImmutablePair<>(token, new ImmutablePair<>(-1, -1)));

                return true;
            }

            if (possibleToken.startsWith(token)) {
                index += token.length();
                PIF.add(new ImmutablePair<>(token, new ImmutablePair<>(-1, -1)));

                return true;
            }
        }
        return false;
    }

    private void nextToken() throws ScannerException{
        discardSpaces();

        if (index == program.length()) {
            return;
        }

        if (treatIdentifier()) {
            return;
        }

        if (treatStringConstant()) {
            return;
        }

        if (treatIntConstant()) {
            return;
        }

        if (treatFromTokenList()) {
            return;
        }

        throw new ScannerException("Lexical error occurred: invalid token at line " + currentLine);
    }

    public void scan(String programFileName){
        try {
            Path file = Path.of("src/main/resources/" + programFileName);
            setProgram(Files.readString(file));

            index = 0;
            PIF = new ArrayList<>();
            symbolTable = new SymbolTable(47);
            currentLine = 1;

            while (index < program.length()) {
                nextToken();
            }

            String pifFileName = "PIF" + programFileName.replace(".txt", ".out");
            FileWriter fileWriter = new FileWriter(pifFileName);

            for (var pair : PIF) {
                fileWriter.write(pair.getKey() + " -> (" + pair.getValue().getKey() + ", " + pair.getValue().getValue() + ")\n");
            }

            fileWriter.close();

            String stFileName = "ST" + programFileName.replace(".txt", ".out");
            fileWriter = new FileWriter(stFileName);
            fileWriter.write(symbolTable.toString());

            fileWriter.close();

            System.out.println("Lexically correct");

        } catch (IOException | ScannerException e) {
            System.out.println(e.getMessage());
        }
    }
}
