package org.spbstu.chernonog.project;


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SplayTreeMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V> {

    private final Comparator<? super K> comparator;

    private int size = 0;

    private int modCount = 0;


    private SplayTreeMap.Entry<K, V> root;


    ///////CONSTRUCTORS

    public SplayTreeMap() {
        comparator = null;
    }

    public SplayTreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    public SplayTreeMap(Map<? extends K, ? extends V> map) {
        comparator = null;
        putAll(map);
    }

    ///////QUERY OPS

    @Override
    public boolean containsValue(Object value) {
        for (SplayTreeMap.Entry<K, V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value))
                return true;
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    @Override
    public V get(Object key) {
        SplayTreeMap.Entry<K, V> p = getEntry(key);
        return (p == null ? null : p.value);
    }

    @Override
    public V remove(Object key) {
        SplayTreeMap.Entry<K, V> p = getEntry(key);
        if (p == null)
            return null;
        V oldValue = p.value;
        mergeDeleteEntry(p);
        return oldValue;
    }

    @Override
    public V put(K key, V value) {
        return splayPut(key, value);
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        root = null;
        modCount++;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        int expectedModCount = modCount;
        for (SplayTreeMap.Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            action.accept(e.key, e.value);
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        SplayTreeMap.Entry<K, V> p = getEntry(key);
        if (p != null && Objects.equals(oldValue, p.value)) {
            p.value = newValue;
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        SplayTreeMap.Entry<K, V> p = getEntry(key);
        if (p != null) {
            return p.setValue(value);
        }
        return null;
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        int expectedModCount = modCount;

        for (SplayTreeMap.Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            e.value = function.apply(e.key, e.value);
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /////////VIEWS
    private EntrySet entrySet;

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(SplayTreeMap.this, getFirstEntry(), null);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object value = entry.getValue();
            SplayTreeMap.Entry<K, V> p = getEntry(entry.getKey());
            return p != null && valEquals(p.getValue(), value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object value = entry.getValue();
            SplayTreeMap.Entry<K, V> p = getEntry(entry.getKey());
            if (p != null && valEquals(p.getValue(), value)) {
                mergeDeleteEntry(p);
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            return SplayTreeMap.this.size();
        }

        @Override
        public void clear() {
            SplayTreeMap.this.clear();
        }
    }


    /////////SORTED MAP METHODS
    @Override
    public K firstKey() {
        return key(splayToRoot(getFirstEntry()));
    }

    @Override
    public K lastKey() {
        return key(splayToRoot(getLastEntry()));
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new SubMap(this, false, fromKey, false, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return new SubMap(this, true, null, false, toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return new SubMap(this, false, fromKey, true, null);
    }

    private static final Object UNBOUNDED = new Object();

    class SubMap extends AbstractMap<K, V> implements SortedMap<K, V> {
        final SplayTreeMap<K, V> m;
        final K lo, hi;
        final boolean fromStart, toEnd;

        SubMap(
                SplayTreeMap<K, V> m,
                boolean fromStart, K lo,
                boolean toEnd, K hi
        ) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    m.compare(lo, lo);
                if (!toEnd)
                    m.compare(hi, hi);
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.toEnd = toEnd;
            this.hi = hi;
        }

        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = m.compare(key, lo);
                return c < 0; // fromKey is inclusive, == 0 is ok
            }
            return false;
        }

        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = m.compare(key, hi);
                return c >= 0; // toKey exclusive, == 0 is not ok
            }
            return false;
        }

        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        final SplayTreeMap.Entry<K, V> subLowest() {
            return (fromStart ? m.getFirstEntry() : m.getCeilingEntry(lo));
        }

        final SplayTreeMap.Entry<K, V> subHighest() {
            return (toEnd ? m.getLastEntry() : m.getLowerEntry(hi));
        }

        final SplayTreeMap.Entry<K, V> highFence() {
            return (toEnd ? null : m.getCeilingEntry(hi));
        }

        // PUBLIC
        @Override
        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        @Override
        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        @Override
        public final boolean containsKey(Object key) {
            return inRange(key) && m.containsKey(key);
        }

        @Override
        public final V put(K key, V value) {
            if (!inRange(key))
                throw new IllegalArgumentException("key out of range");
            return m.put(key, value);
        }

        @Override
        public final V get(Object key) {
            return !inRange(key) ? null : m.get(key);
        }

        @Override
        public final V remove(Object key) {
            return !inRange(key) ? null : m.remove(key);
        }

        // SORTED MAP METHODS
        @Override
        public final K firstKey() {
            return key(m.splayToRoot(subLowest()));
        }

        @Override
        public final K lastKey() {
            return key(m.splayToRoot(subHighest()));
        }

        @Override
        public Comparator<? super K> comparator() {
            return m.comparator;
        }

        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            if (!inRange(fromKey))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey))
                throw new IllegalArgumentException("toKey out of range");
            return new SubMap(m, false, fromKey, false, toKey);
        }

        @Override
        public SortedMap<K, V> headMap(K toKey) {
            if (!inRange(toKey))
                throw new IllegalArgumentException("toKey out of range");
            return new SubMap(m, fromStart, lo, false, toKey);
        }

        @Override
        public SortedMap<K, V> tailMap(K fromKey) {
            if (!inRange(fromKey))
                throw new IllegalArgumentException("fromKey out of range");
            return new SubMap(m, false, fromKey, toEnd, hi);
        }


        //VIEWS
        @Override
        public Set<Entry<K, V>> entrySet() {
            return new SubEntrySet();
        }

        class SubEntrySet extends AbstractSet<Map.Entry<K, V>> {
            private int size = -1;
            private int modCount;

            @Override
            public int size() {
                if (fromStart && toEnd)
                    return m.size();
                if (size == -1 || modCount != m.modCount) {
                    modCount = m.modCount;
                    size = 0;
                    for (Entry<K, V> ignored : this) {
                        size++;
                    }
                }
                return size;
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                SplayTreeMap.Entry<?, ?> node = m.getEntry(key);
                return node != null &&
                        valEquals(node.getValue(), entry.getValue());
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                SplayTreeMap.Entry<K, V> node = m.getEntry(key);
                if (node != null && valEquals(node.getValue(), entry.getValue())) {
                    m.mergeDeleteEntry(node);
                    return true;
                }
                return false;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new EntryIterator(m, subLowest(), highFence());
            }
        }
    }


    // ITERATORS
    final class EntryIterator implements Iterator<Map.Entry<K, V>> {

        SplayTreeMap.Entry<K, V> lastReturned;
        SplayTreeMap.Entry<K, V> next;
        final Object fenceKey;
        SplayTreeMap<K, V> m;
        int expectedModCount;

        EntryIterator(SplayTreeMap<K, V> map,
                      SplayTreeMap.Entry<K, V> first,
                      SplayTreeMap.Entry<K, V> fence) {
            m = map;
            expectedModCount = m.modCount;
            lastReturned = null;
            next = first;
            fenceKey = fence == null ? UNBOUNDED : fence.key;
        }

        public final boolean hasNext() {
            return next != null && next.key != fenceKey;
        }

        @Override
        public SplayTreeMap.Entry<K, V> next() {
            SplayTreeMap.Entry<K, V> e = next;
            if (e == null || e.key == fenceKey)
                throw new NoSuchElementException();
            if (m.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            next = successor(e);
            lastReturned = e;
            return e;
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
//            // deleted entries are replaced by their successors
//            if (lastReturned.left != null && lastReturned.right != null)
//                next = lastReturned;
            mergeDeleteEntry(lastReturned);
            expectedModCount = modCount;
            lastReturned = null;
        }
    }


    //INTERNAL OPS
    private Entry<K, V> getEntry(Object key) {
        if (root == null) {
            return null;
        }
        splayToRoot(findClosest(key));
        if (compare(root.key, key) != 0) {
            return null;
        } else {
            return root;
        }
    }


    final SplayTreeMap.Entry<K, V> findClosest(Object key) {
        return findClosest(root, key);
    }

    final SplayTreeMap.Entry<K, V> findClosest(SplayTreeMap.Entry<K, V> start, Object key) {
        if (comparator != null)
            return findClosestUsingComparator(start, key);
        if (key == null)
            throw new NullPointerException();
        return findClosestNotUsingComparator(start, key);
    }

    final SplayTreeMap.Entry<K, V> findClosestUsingComparator(SplayTreeMap.Entry<K, V> start, Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        SplayTreeMap.Entry<K, V> p = start;
        if (cpr != null) {
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0 && p.left != null)
                    p = p.left;
                else if (cmp > 0 && p.right != null)
                    p = p.right;
                else
                    break;
            }
        }
        return p;
    }

    final SplayTreeMap.Entry<K, V> findClosestNotUsingComparator(SplayTreeMap.Entry<K, V> start, Object key) {
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        SplayTreeMap.Entry<K, V> p = start;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0 && p.left != null)
                p = p.left;
            else if (cmp > 0 && p.right != null)
                p = p.right;
            else
                break;
        }
        return p;
    }


    final void rotate(SplayTreeMap.Entry<K, V> p, SplayTreeMap.Entry<K, V> c) {
        SplayTreeMap.Entry<K, V> gp = p.parent;
        if (gp != null) {
            if (p == gp.left) {
                gp.left = c;
            } else {
                gp.right = c;
            }
        }
        if (p.left == c) {
            p.left = c.right;
            c.right = p;
        } else {
            p.right = c.left;
            c.left = p;
        }
        c.parent = gp;
        p.saveParent();
        c.saveParent();
    }

    final SplayTreeMap.Entry<K, V> splay(SplayTreeMap.Entry<K, V> e) {
        if (e == null) {
            return null;
        }
        if (e.parent == null) {
            return e;
        }
        SplayTreeMap.Entry<K, V> p = e.parent;
        SplayTreeMap.Entry<K, V> gp = p.parent;
        if (gp == null) {
            rotate(p, e);
            return e;
        }
        if ((gp.left == p) == (p.left == e)) { //zig-zig
            rotate(gp, p);
            rotate(p, e);
        } else { //zig-zag
            rotate(p, e);
            rotate(gp, e);
        }
        return splay(e);
    }

    final SplayTreeMap.Entry<K, V> splayToRoot(SplayTreeMap.Entry<K, V> e) {
        root = splay(e);
        return root;
    }


    //merge left and right subtree and return tree root with null parent
    final SplayTreeMap.Entry<K, V> merge(SplayTreeMap.Entry<K, V> l, SplayTreeMap.Entry<K, V> r) {
        if (r == null) {
            return l;
        }
        if (l == null) {
            return r;
        }
        r.parent = null;
        r = splay(findClosest(r, l.key));
        r.left = l;
        l.parent = r;
        return r;
    }

    final V splayPut(K key, V value) {
        if (root == null) {
            compare(key, key);
            root = new SplayTreeMap.Entry<>(key, value, null);
            modCount++;
            size = 1;
            return null;
        }
        SplayTreeMap.Entry<K, V> e = splay(findClosest(key));
        int cmp = compare(e.key, key);
        if (cmp == 0) {
            root = e;
            return root.setValue(value);
        } else {
            if (cmp < 0) {
                SplayTreeMap.Entry<K, V> r = e.right;
                e.right = null;
                root = new SplayTreeMap.Entry<>(key, value, null, e, r);
                root.saveParent();
            } else {
                SplayTreeMap.Entry<K, V> l = e.left;
                e.left = null;
                root = new SplayTreeMap.Entry<>(key, value, null, l, e);
                root.saveParent();
            }
            modCount++;
            size++;
            return null;
        }
    }



    final SplayTreeMap.Entry<K, V> mergeDeleteEntry(SplayTreeMap.Entry<K, V> e) {
        if (e.left != null) {
            e.left.setParent(null);
        }
        if (e.right != null) {
            e.right.setParent(null);
        }
        SplayTreeMap.Entry<K, V> replacement = merge(e.left, e.right);
        SplayTreeMap.Entry<K, V> p = e.parent;

        if (replacement != null) {
            replacement.setParent(p);
        }

        if (p == null) { // deleting root of this tree
            root = replacement;
        } else {
            if (e == p.left) {
                p.left = replacement;
            }
            if (e == p.right) {
                p.right = replacement;
            }
        }
        size--;
        modCount++;
        return replacement;
    }

    final SplayTreeMap.Entry<K, V> getFirstEntry() {
        SplayTreeMap.Entry<K, V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }


    final SplayTreeMap.Entry<K, V> getLastEntry() {
        SplayTreeMap.Entry<K, V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }


    static <K, V> SplayTreeMap.Entry<K, V> successor(SplayTreeMap.Entry<K, V> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            SplayTreeMap.Entry<K, V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            SplayTreeMap.Entry<K, V> p = t.parent;
            SplayTreeMap.Entry<K, V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    static <K, V> SplayTreeMap.Entry<K, V> predecessor(SplayTreeMap.Entry<K, V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            SplayTreeMap.Entry<K, V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            SplayTreeMap.Entry<K, V> p = t.parent;
            SplayTreeMap.Entry<K, V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    // find required or greater
    private Entry<K, V> getCeilingEntry(K k) {
        SplayTreeMap.Entry<K, V> e = findClosest(k);
        while (compare(e.key, k) < 0) {
            e = successor(e);
            if (e == null) {
                break;
            }
        }
        return e;
    }

    // find lower
    private Entry<K, V> getLowerEntry(K k) {
        SplayTreeMap.Entry<K, V> e = findClosest(k);
        while (compare(e.key, k) >= 0) {
            e = predecessor(e);
            if (e == null) {
                break;
            }
        }
        return e;
    }


    //ENTRY
    static final class Entry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        SplayTreeMap.Entry<K, V> left;
        SplayTreeMap.Entry<K, V> right;
        SplayTreeMap.Entry<K, V> parent;

        Entry(K key, V value, SplayTreeMap.Entry<K, V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        Entry(K key, V value, SplayTreeMap.Entry<K, V> parent, SplayTreeMap.Entry<K, V> left, SplayTreeMap.Entry<K, V> right) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.right = right;
            this.left = left;
        }

        public K getKey() {
            return key;
        }


        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

            return valEquals(key, e.getKey()) && valEquals(value, e.getValue());
        }

        public int hashCode() {
            int keyHash = (key == null ? 0 : key.hashCode());
            int valueHash = (value == null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }

        void setParent(SplayTreeMap.Entry<K, V> p) {
            this.parent = p;
        }

        final void saveParent() {
            if (left != null) {
                left.setParent(this);
            }
            if (right != null) {
                right.setParent(this);
            }
        }

    }


    //utils
    @SuppressWarnings("unchecked")
    final int compare(Object k1, Object k2) {
        return comparator == null ? ((Comparable<? super K>) k1).compareTo((K) k2)
                : comparator.compare((K) k1, (K) k2);
    }

    static <K, V> Map.Entry<K, V> exportEntry(SplayTreeMap.Entry<K, V> e) {
        return (e == null) ? null :
                new AbstractMap.SimpleImmutableEntry<>(e);
    }

    static boolean valEquals(Object o1, Object o2) {
        return (Objects.equals(o1, o2));
    }

    static <K> K key(SplayTreeMap.Entry<K, ?> e) {
        if (e == null)
            throw new NoSuchElementException();
        return e.key;
    }
}
