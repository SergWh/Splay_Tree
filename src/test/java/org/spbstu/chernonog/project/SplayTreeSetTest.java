package org.spbstu.chernonog.project;

import org.junit.Before;
import org.junit.Test;


import java.util.Arrays;
import java.util.Iterator;


import static org.junit.Assert.*;

public class SplayTreeSetTest {

    SplayTreeSet<Integer> splayTreeSet;

    @Before
    public void setUp() throws Exception {
        splayTreeSet = new SplayTreeSet<>();
    }

    @Test
    public void nullComparator() {
        assertNull(splayTreeSet.comparator());
    }

    @Test
    public void subSet() {
    }

    @Test
    public void headSet() {
    }

    @Test
    public void tailSet() {
    }

    @Test
    public void first() {
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
    public void last() {
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
    public void size() {
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
    public void isEmpty() {
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
    public void contains() {
        splayTreeSet.add(15);
        assertTrue(splayTreeSet.contains(15));
        assertFalse(splayTreeSet.contains(-2));
    }

    @Test
    public void iterator() {
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
    public void iteratorRemove() {
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(4);
        splayTreeSet.add(5);
        splayTreeSet.add(6);
        Iterator<Integer> iterator = splayTreeSet.iterator();
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            if (i > 4) iterator.remove();
        }
        System.out.println((splayTreeSet.contains(6)));
    }

    @Test
    public void add() {

    }

    @Test
    public void remove() {
    }

    @Test
    public void containsAll() {
    }

    @Test
    public void addAll() {
    }

    @Test
    public void retainAll() {
    }

    @Test
    public void removeAll() {
    }

    @Test
    public void clear() {
        splayTreeSet.add(2);
        splayTreeSet.add(6);
        assertFalse(splayTreeSet.isEmpty());
        splayTreeSet.clear();
        assertTrue(splayTreeSet.isEmpty());
    }
}