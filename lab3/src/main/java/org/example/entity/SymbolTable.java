package org.example.entity;

import org.apache.commons.lang3.tuple.Pair;
import org.example.ds.HashTable;
import org.example.ds.HashableDS;

public class SymbolTable {
    private int size;

    private HashableDS<String> identifiersTable;
    private HashableDS<String> stringConstantsTable;
    private HashableDS<Integer> intConstantsTable;

    public SymbolTable(int size) {
        this.size = size;

        this.identifiersTable = new HashTable<>(size);
        this.stringConstantsTable = new HashTable<>(size);
        this.intConstantsTable = new HashTable<>(size);
    }

    public Pair<Integer, Integer> addIdentifier(String name) throws Exception {
        return this.identifiersTable.add(name);
    }

    public Pair<Integer, Integer> addIntConstant(int constant) throws Exception {
        return this.intConstantsTable.add(constant);
    }

    public Pair<Integer, Integer> addStringConstant(String constant) throws Exception {
        return this.stringConstantsTable.add(constant);
    }

    public boolean hasIdentifier(String name) {
        return this.identifiersTable.contains(name);
    }

    public boolean hasIntConstant(int constant) {
        return this.intConstantsTable.contains(constant);
    }

    public boolean hasStringConstant(String constant) {
        return this.stringConstantsTable.contains(constant);
    }

    public Pair<Integer, Integer> getPositionIdentifier(String name) {
        return this.identifiersTable.getPosition(name);
    }

    public Pair<Integer, Integer> getPositionIntConstant(int constant) {
        return this.intConstantsTable.getPosition(constant);
    }

    public Pair<Integer, Integer> getPositionStringConstant(String constant) {
        return this.stringConstantsTable.getPosition(constant);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public HashableDS<String> getIdentifiersTable() {
        return identifiersTable;
    }

    public void setIdentifiersTable(HashableDS<String> identifiersTable) {
        this.identifiersTable = identifiersTable;
    }

    public HashableDS<String> getStringConstantsTable() {
        return stringConstantsTable;
    }

    public void setStringConstantsTable(HashableDS<String> stringConstantsTable) {
        this.stringConstantsTable = stringConstantsTable;
    }

    public HashableDS<Integer> getIntConstantsTable() {
        return intConstantsTable;
    }

    public void setIntConstantsTable(HashableDS<Integer> intConstantsTable) {
        this.intConstantsTable = intConstantsTable;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "identifiersHashTable=" + this.identifiersTable +
                "\nintConstantsHashTable=" + this.intConstantsTable +
                "\nstringConstantsHashTable=" + this.stringConstantsTable +
                '}';
    }
}
