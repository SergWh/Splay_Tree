package org.spbstu.chernonog.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SplayTreeMapSortedTest {
    SplayTreeMap<Integer, String> map;
    ArrayList<Integer> keys;

    @BeforeEach
    void setUp() {
        map = new SplayTreeMap<>();
        keys = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(keys);
        keys.forEach(key -> map.put(key, key.toString()));
    }

    @Test
    void firstKey() {
        assertEquals(Collections.min(keys), map.firstKey());
    }

    @Test
    void lastKey() {
        assertEquals(Collections.max(keys), map.lastKey());
    }

    @Test
    void iteratorOrder() {
        List<Integer> sortedKeys = new ArrayList<>();
        Collections.sort(keys);
        map.keySet().forEach(key -> sortedKeys.add(key));
        assertEquals(keys, sortedKeys);
    }

    @Test
    void subMapExceptions() {
        assertThrows(IllegalArgumentException.class, () -> map.subMap(5, 2));
        assertThrows(NullPointerException.class, () -> map.subMap(null, 2));
        assertThrows(NullPointerException.class, () -> map.subMap(2, null));
        assertThrows(NullPointerException.class, () -> map.tailMap(null));
        assertThrows(NullPointerException.class, () -> map.headMap(null));
    }


    @Test
    void subSubMapException() {
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).subMap(1, 5));
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).subMap(3, 6));
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).tailMap(6));
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).headMap(6));
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).tailMap(1));
        assertThrows(IllegalArgumentException.class, () -> map.subMap(2, 5).headMap(1));
    }

    @Test
    void subPut() {
        Map<Integer, String> subMap = map.subMap(-1, 20);
        assertNull(subMap.put(12, "12"));
        assertTrue(map.containsKey(12));
        assertTrue(map.containsValue("12"));
    }

    @Test
    void subContains() {
        Map<Integer, String> subMap = map.subMap(-1, 20);
        assertTrue(subMap.containsValue("1"));
        assertFalse(subMap.containsValue("nope"));
        assertTrue(subMap.containsKey(1));
        assertFalse(subMap.containsKey(-1));
    }

    @Test
    void subRemove() {
        Map<Integer, String> subMap = map.subMap(-1, 20);
        assertEquals("1", subMap.remove(1));
        assertNull(map.get(1));

        assertEquals("7", map.remove(7));
        assertNull(subMap.get(7));
    }

    @Test
    void subSubMap() {
        Map<Integer, String> subMap = map.subMap(2, 20);
        assertNull(map.subMap(3, 12).put(11, "11"));
        assertTrue(map.containsKey(11));
        assertTrue(subMap.containsKey(11));
    }

    @Test
    void subFirstLast() {
        int hi = 6;
        int lo = 2;
        SortedMap<Integer, String> subMap = map.subMap(lo, hi);
        assertTrue(hi > subMap.lastKey());
        assertTrue(lo <= subMap.firstKey());
    }

}
