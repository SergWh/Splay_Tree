package org.spbstu.chernonog.project;

import org.junit.Before;
import org.junit.Test;


import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;


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
        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(5);
        splayTreeSet.add(8);
        splayTreeSet.add(9);
        splayTreeSet.add(-1);
        splayTreeSet.add(0);
        SortedSet<Integer> subSet = splayTreeSet.subSet(0, 4);
        assertEquals(0, subSet.first().intValue());
        assertEquals(2, subSet.last().intValue());
        subSet.remove(2);
        assertFalse(subSet.contains(2));
        assertFalse(splayTreeSet.contains(2));
    }

    @Test
    public void subSubSet(){
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
        SortedSet<Integer> subSubSet = subSet.subSet(1,3);
        assertEquals(1, subSubSet.first().intValue());
        assertEquals(2, subSubSet.last().intValue());
        subSubSet.remove(1);
        assertFalse(splayTreeSet.contains(1));
        assertFalse(subSet.contains(1));
        assertFalse(subSubSet.contains(1));
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
        try {
            splayTreeSet.add(null);
        } catch (NullPointerException e) {

        }

    }

    @Test
    public void remove() {

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
