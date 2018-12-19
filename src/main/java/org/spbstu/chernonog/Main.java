package org.spbstu.chernonog;


import org.spbstu.chernonog.allthewaynew.SplayTree;
import org.spbstu.chernonog.project.SplayTreeSet;

import java.util.*;

public class Main {


    public static void main(final String[] args) {


        Comparator<Character> characterComparator = new Comparator<Character>() {
            String alphabet = "abcdefghikjlmnoprqrstuvwxyz";

            @Override
            public int compare(Character o1, Character o2) {
                return Integer.compare(alphabet.indexOf(o1), alphabet.indexOf(o2));
            }
        };
        SplayTreeSet<Integer> splayTreeSet = new SplayTreeSet<>();

//        System.out.println(set.add(1));
//        System.out.println(set.add(2));
//        System.out.println(set.add(24));
//        System.out.println("shoopdawoop");
//        System.out.println(set.add(2));
//        System.out.println(set.size());
//        System.out.println(set.contains(1));
//        System.out.println(set.contains(2));
//        System.out.println(set.contains(24));
//        System.out.println(set.size());
//        System.out.println(set.remove(2));
//        System.out.println(set.contains(2));
//        System.out.println(set.size());
//        System.out.println(set.first());
//        System.out.println(set.last());
//
//        System.out.println("iter");
//
//
//
//        Set<Integer> set1 = new HashSet<>();
//        set1.add(5);
//        set1.add(3);
//        set1.add(8);
//        System.out.println(set.addAll(set1));
//        System.out.println("iter");
//        for (Integer el : set){
//            System.out.println(el);
//        }

        splayTreeSet.add(1);
        splayTreeSet.add(2);
        splayTreeSet.add(3);
        splayTreeSet.add(4);
        splayTreeSet.add(5);
        splayTreeSet.add(6);
        Iterator<Integer> iterator = splayTreeSet.iterator();
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            if (i % 2 == 0) iterator.remove();
        }
        System.out.println(splayTreeSet);
        System.out.println(splayTreeSet.getRoot());
        System.out.println(splayTreeSet.contains(6));
    }
}