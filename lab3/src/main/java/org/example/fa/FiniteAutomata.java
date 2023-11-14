package org.example.fa;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class FiniteAutomata {
    private List<String> states;
    private List<String> alphabet;
    private HashMap<Pair<String, String>, String> transitionsV2;
    private String initialState;
    private List<String> outputStates;
    private String filename;

    public FiniteAutomata(String filename) {
        this.filename = filename;
        this.states = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.transitionsV2 = new HashMap<>();
        this.initialState = "";
        this.outputStates = new ArrayList<>();

        try {
            init();
        } catch (Exception e) {
            System.out.println("Error when initializing Finite Automata");
        }
    }

    private String getParsedData(String line) {
        var initialStringData = line.substring(line.indexOf("=") + 1);

        return initialStringData.substring(1, initialStringData.length() - 1).trim();
    }

    private void handleStates(String line) {
        String states = this.getParsedData(line);
        this.states = List.of(states.split(", *"));
    }

    private void handleAlphabet(String line) {
        String alphabet = this.getParsedData(line);
        this.alphabet = List.of(alphabet.split(", *"));
    }

    private void handleOutStates(String line) {
        String outputStates = this.getParsedData(line);

        this.outputStates = List.of(outputStates.split(", *"));
    }

    private void handleInitialState(String line) {
        this.initialState = line.substring(line.indexOf("=") + 1).trim();
    }

    private void handleTransitions(String line) {
        String transitions = this.getParsedData(line);
        var transitionsList = List.of(transitions.split("; *"));

        for (String transition: transitionsList) {
            var transitionWithoutParantheses = transition.substring(1, transition.length() - 1).trim();
            var individualValues = List.of(transitionWithoutParantheses.split(", *"));

            String from = individualValues.get(0);
            String to = individualValues.get(1);
            String label = individualValues.get(2);

            Pair<String, String> key = new ImmutablePair<>(from, label);
            this.transitionsV2.put(key, to);
        }
    }

    private void init() throws Exception {
        var regex = Pattern.compile("^([a-z_]*)=");

        for (String line: Files.readAllLines(Paths.get(filename))) {
            var matcher = regex.matcher(line);
            var match = matcher.find();

            if (matcher.group(0) == null) {
                throw new Exception("Invalid line: " + line);
            }

            switch (matcher.group(0)) {
                case "states=" -> this.handleStates(line);
                case "alphabet=" -> this.handleAlphabet(line);
                case "out_states=" -> this.handleOutStates(line);
                case "initial_state=" -> this.handleInitialState(line);
                case "transitions=" -> this.handleTransitions(line);
                default -> throw new Exception("Invalid line in file");
            }
        }
    }

    private void printListOfString(String listname, List<String> list) {
        System.out.print(listname + " = {");

        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                System.out.print(list.get(i) + ", ");
            } else {
                System.out.print(list.get(i));
            }
        }

        System.out.println("}");
    }

    public void printStates() {
        printListOfString("states", states);
    }

    public void printAlphabet() {
        printListOfString("alphabet", alphabet);
    }

    public void printOutputStates() {
        printListOfString("out_states", outputStates);
    }

    public void printInitialState() {
        System.out.println("initial_state = " + initialState);
    }

    public void printTransitions() {
        System.out.print("transitions = {\n");

        for (Pair<String, String> key : this.transitionsV2.keySet()) {
            System.out.println("(" + key.getLeft() + ", " + this.transitionsV2.get(key) + ", " + key.getRight() + "); ");
        }

        System.out.println("}");
    }

    public boolean checkAccepted(String word) {
        List<String> wordAsList = List.of(word.split(""));
        var currentState = initialState;

        for (String c: wordAsList) {
            Pair<String, String> key = new ImmutablePair<>(currentState, c);

            if (!this.transitionsV2.containsKey(key)) {
                return false;
            }

            currentState = this.transitionsV2.get(key);
        }

        return outputStates.contains(currentState);
    }

    public String getNextAccepted(String word) {
        var currentState = initialState;
        StringBuilder acceptedWord = new StringBuilder();

        for (String c: word.split("")) {
            String newState = null;

            Pair<String, String> key = new ImmutablePair<>(currentState, c);

            if (this.transitionsV2.containsKey(key)) {
                newState = this.transitionsV2.get(key);
                acceptedWord.append(c);
            }

            if (newState == null) {
                if (!outputStates.contains(currentState)) {
                    return null;
                } else {
                    return acceptedWord.toString();
                }
            }

            currentState = newState;
        }

        return acceptedWord.toString();
    }
}
