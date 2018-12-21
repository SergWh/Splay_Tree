package org.spbstu.chernonog.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SplayTreeSetTest {
    SplayTreeSet<Integer> splayTreeSet;

    @BeforeEach
    void setUp() {
        splayTreeSet = new SplayTreeSet<>();
    }

    @Test
    void size() {
        assertEquals(0, splayTreeSet.size());
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        assertEquals(7, splayTreeSet.size());
    }

    @Test
    void isEmpty() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        assertFalse(splayTreeSet.isEmpty());
        assertTrue(new SplayTreeSet<Integer>().isEmpty());
    }

    @Test
    void contains() {
        assertFalse(splayTreeSet.contains(0));
        splayTreeSet.add(15);
        assertTrue(splayTreeSet.contains(15));
        assertFalse(splayTreeSet.contains(-2));
    }

    @Test
    void containsNull() {
        assertThrows(NullPointerException.class, () -> splayTreeSet.contains(null));
    }

    @Test
    void iterator() {
        int[] nums = {2, 5, 7, 8, 3, 0};
        for (Integer integer : nums) {
            splayTreeSet.add(integer);
        }
        Arrays.sort(nums);
        int i = 0;
        for (Integer integer : splayTreeSet) {
            assertEquals(integer.intValue(), nums[i]);
            i++;
        }
    }

    @Test
    void iteratorRemove() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(4);
        splayTreeSet.add(5);
        splayTreeSet.add(6);
        splayTreeSet.removeIf(i -> i > 4);
        assertFalse(splayTreeSet.contains(6));
    }

    @Test
    void add() {
        splayTreeSet.add(2);
        assertFalse(splayTreeSet.add(2));
        assertTrue(splayTreeSet.add(-1));
    }

    @Test
    void addNull() {
        assertThrows(NullPointerException.class, () -> splayTreeSet.add(null));
    }

    @Test
    void remove() {
        splayTreeSet.add(1);
        splayTreeSet.add(56);
        splayTreeSet.add(23);
        assertFalse(splayTreeSet.remove(-23));
        assertTrue(splayTreeSet.remove(23));
        assertFalse(splayTreeSet.contains(23));
    }

    @Test
    void removeNull() {
        assertThrows(NullPointerException.class, () -> splayTreeSet.remove(null));
    }

    @Test
    void containsAll() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        splayTreeSet.add(2);
        assertFalse(splayTreeSet.containsAll(list));
    }

    @Test
    void containsAllWithNull() {
        List<Integer> list = new ArrayList<>();
        list.add(null);
        list.add(3);
        list.add(4);
        assertThrows(NullPointerException.class, () -> splayTreeSet.containsAll(list));
    }

    @Test
    void addAll() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        assertTrue(splayTreeSet.addAll(list));
        list.remove(2);
        list.add(7);
        assertTrue(splayTreeSet.addAll(list));
        assertFalse(splayTreeSet.addAll(list));
    }

    @Test
    void removeAll() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(4);
        splayTreeSet.add(5);
        assertTrue(splayTreeSet.removeAll(list));
        assertTrue(splayTreeSet.contains(5));
        assertFalse(splayTreeSet.contains(2));
    }

    @Test
    void clear() {
        splayTreeSet.add(2);
        splayTreeSet.add(6);
        assertFalse(splayTreeSet.isEmpty());
        splayTreeSet.clear();
        assertTrue(splayTreeSet.isEmpty());
    }

    @Test
    void subSet() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(4);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        SortedSet<Integer> subSet = splayTreeSet.subSet(0, 4);
        SortedSet<Integer> subSubSet = subSet.subSet(1, 3);

        assertTrue(splayTreeSet.contains(1));
        assertTrue(subSet.contains(1));
        assertTrue(subSubSet.contains(1));

        subSubSet.remove(1);
        assertFalse(splayTreeSet.contains(1));
        assertFalse(subSet.contains(1));
        assertFalse(subSubSet.contains(1));

        splayTreeSet.remove(2);
        assertFalse(splayTreeSet.contains(2));
        assertFalse(subSet.contains(2));
        assertFalse(subSubSet.contains(2));
    }

    @Test
    void illegalArgumentsSubSet() {
        assertThrows(IllegalArgumentException.class, () -> splayTreeSet.subSet(5, 2));
        assertThrows(IllegalArgumentException.class, () -> splayTreeSet.subSet(0, 3).subSet(4, 10));
    }

    @Test
    void nullArgumentSubSet() {
        assertThrows(NullPointerException.class, () -> splayTreeSet.tailSet(null));
        assertThrows(NullPointerException.class, () -> splayTreeSet.headSet(null));
        assertThrows(NullPointerException.class, () -> splayTreeSet.subSet(null, 212));
    }

    @Test
    void firstInEmpty() {
        assertThrows(NoSuchElementException.class, () -> splayTreeSet.first());
    }


    @Test
    void first() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        assertEquals(-1, splayTreeSet.first().intValue());
    }


    @Test
    void last() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        assertEquals(9, splayTreeSet.last().intValue());
    }

    @Test
    void lastInEmpty() {
        assertThrows(NoSuchElementException.class, () -> splayTreeSet.last());
    }

    @Test
    void subContains(){
        assertFalse(splayTreeSet.subSet(0,10).contains(4));
    }

    @Test
    void subIterator() {
        int[] nums = {0, 2, 1, 3, 7, 6, 5, 4};
        for (Integer integer : nums) {
            splayTreeSet.add(integer);
        }
        Arrays.sort(nums);
        SortedSet<Integer> subSet = splayTreeSet.subSet(1, 6);
        SortedSet<Integer> subSubSet = subSet.subSet(2, 5);
        int i = 0;
        for (Integer integer : splayTreeSet) {
            assertEquals(integer.intValue(), nums[i]);
            i++;
        }
        i = 1;
        for (Integer integer : subSet) {
            assertEquals(integer.intValue(), nums[i]);
            i++;
        }
        i = 2;
        for (Integer integer : subSubSet) {
            assertEquals(integer.intValue(), nums[i]);
            i++;
        }
    }

    @Test
    void subAddAll() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        SortedSet<Integer> subSet = splayTreeSet.subSet(0, 5);
        assertTrue(subSet.addAll(list));
        assertTrue(splayTreeSet.contains(2));
    }

    @Test
    void subSetRemove() {
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(7);
        assertEquals(3, splayTreeSet.size());
    }

    @Test
    void subSetClear() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        SortedSet<Integer> subSet = splayTreeSet.subSet(4, 10);
        subSet.clear();
        assertTrue(subSet.isEmpty());
        assertFalse(splayTreeSet.isEmpty());
    }


}