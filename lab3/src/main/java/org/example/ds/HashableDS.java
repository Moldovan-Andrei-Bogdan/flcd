package org.example.ds;

import org.apache.commons.lang3.tuple.Pair;

public interface HashableDS<T> {
    void init(int size);

    int getHashValue(T key);

    Pair<Integer, Integer> add(T key) throws Exception;

    boolean contains(T key);

    Pair<Integer, Integer> getPosition(T key);
}
