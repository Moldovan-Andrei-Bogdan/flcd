package org.example.ds;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class HashTable<T> implements HashableDS<T>{
    private List<List<T>> items;

    private int size;

    public HashTable(int size) {
        this.size = size;
        this.init(size);
    }

    public void init(int size) {
        this.items = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            this.items.add(new ArrayList<>());
        }
    }

    private int hash(int key) {
        return key % size;
    }

    private int hash(String key) {
        int sum = 0;

        for (int i = 0; i < key.length(); i++) {
            sum += key.charAt(i);
        }

        return sum % size;
    }

    public int getHashValue(T key) {
        int hashValue = -1;

        if (key instanceof Integer) {
            hashValue = hash((int) key);
        }

        if (key instanceof String) {
            hashValue = hash((String) key);
        }

        return hashValue;
    }

    public Pair<Integer, Integer> add(T key) throws Exception {
        int hashValue = this.getHashValue(key);

        if (items.get(hashValue).contains(key)) {
            throw new Exception("Key " + key + " is already in the table!");
        }

        this.items.get(hashValue).add(key);
        int keyPosition = this.items.get(hashValue).indexOf(key);

        return new ImmutablePair<>(hashValue, keyPosition);
    }

    public boolean contains(T key) {
        int hashValue = this.getHashValue(key);
        return items.get(hashValue).contains(key);
    }

    public Pair<Integer, Integer> getPosition(T key) {
        if (!this.contains(key)) {
            return new ImmutablePair<>(-1, -1);
        }

        int hashValue = this.getHashValue(key);
        int keyPosition = this.items.get(hashValue).indexOf(key);

        return new ImmutablePair<>(hashValue, keyPosition);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "HashTable{" + "items=" + items + '}';
    }
}
