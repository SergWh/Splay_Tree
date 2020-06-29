package org.spbstu.chernonog.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SplayTreeMapQueryTest {

    SplayTreeMap<Integer, String> map;
    TreeMap<Integer, String> expMap;
    ArrayList<Integer> keys;

    @BeforeEach
    void setUp() {
        map = new SplayTreeMap<>();
        keys = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(keys);
        keys.forEach(key -> map.put(key, key.toString()));
    }

    @Test
    void get() {
        keys.forEach(key ->
                assertEquals(key.toString(), map.get(key))
        );
        assertNull(map.get(-1));
        map.put(-1, "-1");
        keys.forEach(key ->
                assertEquals(key.toString(), map.get(key))
        );
        assertEquals("-1", map.get(-1));
    }

    @Test
    void getException() {
        assertThrows(ClassCastException.class, () -> map.get("as"));
    }

    @Test
    void remove() {
        assertFalse(map.remove(2, "not 2"));
        assertEquals("2", map.remove(2));
        keys.removeIf(key -> key == 2);
        keys.forEach(key ->
                assertEquals(key.toString(), map.get(key))
        );
    }

    @Test
    void removeException() {
        assertThrows(ClassCastException.class, () -> map.remove("as"));
    }

    @Test
    void put() {
        assertEquals("2", map.put(2, "not 2"));
        assertNull(map.put(-1, "lol"));
    }


    @Test
    void containsValue() {
        assertTrue(map.containsValue("1"));
        assertFalse(map.containsValue("nope"));
    }

    @Test
    void containsKey() {
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(-1));
    }

    @Test
    void containsKeyException() {
        assertThrows(ClassCastException.class, () -> map.containsKey("as"));
    }

    @Test
    void clear() {
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    void forEach() {
        map.forEach((k, v) -> assertTrue(keys.contains(k)));
    }

    @Test
    void replace() {
        assertTrue(map.replace(1, "1", "2"));
        assertFalse(map.replace(1, "1", "2"));
        assertEquals("2", map.replace(2, "4"));
    }

    @Test
    void replaceAll() {
        map.replaceAll((k, v) -> "bla");
        keys.forEach(key ->
                assertEquals("bla", map.get(key))
        );
    }


}