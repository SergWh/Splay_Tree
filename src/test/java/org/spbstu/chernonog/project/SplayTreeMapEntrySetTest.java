package org.spbstu.chernonog.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SplayTreeMapEntrySetTest {

    SplayTreeMap<Integer, String> map;
    ArrayList<Integer> keys;
    Set<Map.Entry<Integer, String>> entrySet;

    @BeforeEach
    void setUp() {
        map = new SplayTreeMap<>();
        keys = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(keys);
        keys.forEach(key -> map.put(key, key.toString()));
        entrySet = map.entrySet();
    }

    @Test
    void contains() {
        assertTrue(entrySet.contains(map.getFirstEntry()));
        assertFalse(entrySet.contains(1));
    }

    @Test
    void remove() {
        Map.Entry<Integer, String> e = map.getFirstEntry();
        assertTrue(entrySet.remove(e));
        assertFalse(entrySet.contains(e));
        assertFalse(map.containsKey(e.getKey()));
        assertFalse(map.containsValue(e.getValue()));
        assertFalse(entrySet.remove(1));
    }

    @Test
    void iteratorRemove() {
        entrySet.removeIf(e -> e.getKey() % 2 == 0);
        keys.removeIf(key -> key % 2 == 0);
        keys.forEach(key ->
                assertEquals(key.toString(), map.get(key))
        );
    }

    @Test
    void subContains() {
        assertTrue(map.subMap(2, 12).entrySet().contains(map.getLastEntry()));
        assertFalse(map.subMap(2, 3).entrySet().contains(map.getLastEntry()));
    }

    @Test
    void subRemove() {
        assertFalse(map.subMap(2, 3).entrySet().remove(map.getLastEntry()));
        assertTrue(map.entrySet().contains(map.getLastEntry()));

        assertTrue(map.subMap(2, 12).entrySet().remove(map.getLastEntry()));
        assertFalse(map.containsKey(8));
    }

    @Test
    void subIteratorRemove() {
        map.subMap(2, 6).entrySet().removeIf(e -> e.getKey() % 2 == 0);
        keys.removeIf(key -> key >= 2 && key < 6 && key % 2 == 0);
        keys.forEach(key ->
                assertEquals(key.toString(), map.get(key))
        );
    }
}
