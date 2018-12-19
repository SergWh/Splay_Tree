package org.spbstu.chernonog.project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SplayTreeSet<E> extends AbstractSet<E>
        implements SortedSet<E> {

    private int compare(Object first, E second) {
        if (comparator == null) {
            Comparable<? super E> f = (Comparable<? super E>) first;
            return f.compareTo(second);
        } else {
            E f = (E) first;
            return comparator.compare(f, second);
        }
    }

    private void setParent(Node<E> child, Node<E> parent) {
        if (child != null) {
            child.parent = parent;
        }
    }

    private void saveParent(Node<E> node) {
        setParent(node.left, node);
        setParent(node.right, node);
    }

    private void rotate(Node<E> parent, Node<E> child) {
        Node<E> grandparent = parent.parent;
        if (grandparent != null) {
            if (parent == grandparent.left) {
                grandparent.left = child;
            } else {
                grandparent.right = child;
            }
        }
        if (parent.left == child) {
            parent.left = child.right;
            child.right = parent;
        } else {
            parent.right = child.left;
            child.left = parent;
        }
        child.parent = grandparent;
        saveParent(parent);
        saveParent(child);
    }


    private Node<E> splay(Node<E> entry) {
        if (entry.parent == null) {
            return entry;
        }
        Node<E> parent = entry.parent;
        Node<E> grandparent = parent.parent;
        if (grandparent == null) {
            rotate(parent, entry);
            return entry;
        }
        if ((grandparent.left == parent) == (grandparent.right == parent)) {
            rotate(grandparent, parent);
            rotate(parent, entry);
        } else {
            rotate(parent, entry);
            rotate(grandparent, entry);
        }
        return splay(entry);
    }

    private Node<E> find(Node<E> node, E element) {
        if (node == null) {
            return null;
        }
        int cmp = compare(element, node.element);
        if (cmp > 0 && node.right != null) {
            return find(node.right, element);
        }
        if (cmp < 0 && node.left != null) {
            return find(node.left, element);
        }
        return node;
    }

    private boolean splayInsert(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        Node<E> first;
        Node<E> second;
        Node<E> node;
        boolean res = true;
        if (root == null) {
            first = null;
            second = null;
        } else {
            node = splay(find(root, element));
            int cmp = compare(node.element, element);
            if (cmp == 0) {
                setParent(node.right, null);
                setParent(node.left, null);
                first = node.left;
                second = node.right;
                res = false;
            } else if (cmp < 0) {
                Node<E> right = node.right;
                node.right = null;
                setParent(right, null);
                first = node;
                second = right;
            } else {
                Node<E> left = node.left;
                node.left = null;
                setParent(left, null);
                first = left;
                second = node;
            }
        }
        node = new Node<E>(element, null, first, second);
        saveParent(node);
        root = node;
        if (res) size++;
        return res;
    }

    private Node<E> merge(Node<E> left, Node<E> right) {
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }
        right = splay(find(right, left.element));
        right.left = left;
        left.parent = right;
        return right;
    }

    private boolean delete(E element) {
        boolean res;
        Node<E> node;
        node = splay(find(root, element));
        int cmp = compare(element, node.element);
        if (cmp != 0) {
            root = node;
            res = false;
        } else {
            setParent(node.left, null);
            setParent(node.right, null);
            root = merge(node.left, node.right);
            res = true;
        }
        if (res) size--;
        return res;
    }


    private Node<E> getFirstNode() {
        Node<E> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    private Node<E> getLastNode() {
        Node<E> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    private boolean nonSplayInsert(E element) {
        Node<E> closest = find(root, element);
        int cmp = compare(element, closest.element);
        if (cmp == 0) {
            return false;
        }
        Node<E> node = new Node<>(element, closest, null, null);
        if (cmp < 0) {
            assert closest.left == null;
            closest.left = node;
        } else {
            assert closest.right == null;
            closest.right = node;
        }
        size++;
        return true;
    }

    private void iteratorDelete(Node<E> node) {
        Node<E> replacement = merge(node.left, node.right);
        Node<E> parent = node.parent;
        setParent(replacement, parent);
        if (parent != null) {
            if (parent.left == node) {
                parent.left = replacement;
            }
            if (parent.right == node) {
                parent.right = replacement;
            }
        } else {
            root = replacement;
        }
        size--;
    }

    static <E> Node<E> successor(Node<E> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            Node<E> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Node<E> p = t.parent;
            Node<E> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    static <E> Node<E> predecessor(Node<E> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Node<E> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Node<E> p = t.parent;
            Node<E> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }


    private final Comparator<? super E> comparator;

    private Node<E> root;

    private int size = 0;

    public SplayTreeSet() {
        this.comparator = null;
    }

    public SplayTreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Nullable
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @NotNull
    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (compare(fromElement, toElement) >= 0) {
            throw new IllegalArgumentException();
        }
        return new SubSet(this, fromElement, toElement);
    }

    @NotNull
    @Override
    public SortedSet<E> headSet(E toElement) {
        if (toElement == null) {
            throw new IllegalArgumentException();
        }
        return new SubSet(this, null, toElement);
    }

    @NotNull
    @Override
    public SortedSet<E> tailSet(E fromElement) {
        if (fromElement == null) {
            throw new IllegalArgumentException();
        }
        return new SubSet(this, fromElement, null);
    }

    @Override
    public E first() {
        Node<E> node = getFirstNode();
        if (node == null) throw new NoSuchElementException();
        return node.element;
    }

    @Override
    public E last() {
        Node<E> node = getLastNode();
        if (node == null) throw new NoSuchElementException();
        return node.element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Object key) {
        E k = (E) key;
        root = splay(find(root, k));
        return compare(root.element, k) == 0;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new ElementsIterator(getFirstNode());
    }

    @Override
    public boolean add(E e) {
        return splayInsert(e);
    }

    @Override
    public boolean remove(Object o) {
        E element = (E) o;
        return delete(element);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object element : c) {
            Node<E> node = find(root, (E) element);
            if (compare(element, node.element) != 0) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (nonSplayInsert(e))
                modified = true;
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (Iterator<?> i = iterator(); i.hasNext(); ) {
            if (!c.contains(i.next())) {
                i.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (Iterator<?> i = iterator(); i.hasNext(); ) {
            if (c.contains(i.next())) {
                i.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    public Node<E> getRoot() {
        return root;
    }

    static final class Node<E> {
        E element;
        Node<E> left;
        Node<E> right;
        Node<E> parent;

        Node(E element, Node<E> parent, Node<E> left, Node<E> right) {
            this.element = element;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public E getElement() {
            return element;
        }

        public int hashCode() {
            int valueHash = (element == null ? 0 : element.hashCode());
            return valueHash * 31;
        }

        public String toString() {
            return element.toString();
        }
    }

    class ElementsIterator implements Iterator<E> {
        Node<E> next;
        Node<E> lastReturned;

        ElementsIterator(Node<E> first) {
            lastReturned = null;
            next = first;
        }

        public boolean hasNext() {
            return next != null;
        }

        final Node<E> nextNode() {
            Node<E> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            next = successor(e);
            lastReturned = e;
            return e;
        }

        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            iteratorDelete(lastReturned);
            lastReturned = null;
        }

        @Override
        public E next() {
            return nextNode().element;
        }
    }

    class SubIterator extends ElementsIterator
            implements Iterator<E> {

        Node<E> limit;
        SubSet subSet;

        SubIterator(Node<E> first, Node<E> limit, SubSet subSet) {
            super(first);
            this.limit = limit;
            this.subSet = subSet;
        }

        @Override
        public boolean hasNext() {
            return (next != null && lastReturned != limit);
        }

        @Override
        public void remove() {
            super.remove();
            subSet.size--;
        }
    }


    class SubSet extends AbstractSet<E>
            implements SortedSet<E> {

        SplayTreeSet<E> m;
        E low;
        E high;

        int size;


        private boolean inRange(E element) {
            if (low != null && high != null) return (m.compare(element, low) >= 0 && m.compare(element, high) < 0);
            if (low == null) return (m.compare(element, high) < 0);
            return (m.compare(element, low) >= 0);
        }

        SubSet(SplayTreeSet<E> m, E low, E high) {
            this.m = m;
            this.low = low;
            this.high = high;
        }


        @Nullable
        @Override
        public Comparator<? super E> comparator() {
            return m.comparator();
        }

        @NotNull
        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {

            return m.subSet(fromElement, toElement);
        }


        @NotNull
        @Override
        public SortedSet<E> headSet(E toElement) {
            return null;
        }

        @NotNull
        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return null;
        }

        private Node<E> getFirstNode() {
            if (low == null) return m.getFirstNode();
            Node<E> node = m.find(m.root, low);
            while (!inRange(node.element)) {
                node = successor(node);
                if (node == null) {
                    throw new NoSuchElementException();
                }
            }
            return node;
        }

        private Node<E> getLastNode() {
            if (high == null) return m.getLastNode();
            Node<E> node = m.find(m.root, high);
            while (!inRange(node.element)) {
                node = predecessor(node);
                if (node == null) {
                    throw new NoSuchElementException();
                }
            }
            return node;
        }

        @Override
        public E first() {
            return getFirstNode().element;
        }

        @Override
        public E last() {
            return getLastNode().element;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public boolean isEmpty() {
            return this.size == 0;
        }

        @Override
        public boolean contains(Object o) {
            E element = (E) o;
            if (!inRange(element)) {
                return false;
            }
            return m.contains(element);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new SubIterator(getFirstNode(), getLastNode(), this);
        }

        @Override
        public boolean add(E e) {
            if (!inRange(e)) {
                throw new IllegalArgumentException();
            }
            boolean res = m.add(e);
            if (res) {
                this.size++;
            }
            return res;
        }


        @Override
        public boolean remove(Object o) {
            E element = (E) o;
            if (!inRange(element)) {
                throw new IllegalArgumentException();
            }
            boolean res = m.remove(element);
            if (res) {
                this.size--;
            }
            return res;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            for (Object o : c) {
                E element = (E) o;
                if (!inRange(element)) {
                    throw new IllegalArgumentException();
                }
                Node<E> node = find(root, element);
                if (compare(element, node.element) != 0) return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            boolean modified = false;
            for (E e : c) {
                if (!inRange(e)) {
                    throw new IllegalArgumentException();
                }
                if (nonSplayInsert(e)) {
                    this.size++;
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                E next = (E) i.next();
                if (!c.contains(next) && inRange(next)) {
                    i.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                E next = (E) i.next();
                if (c.contains(next) && inRange(next)) {
                    i.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                i.next();
                i.remove();
            }
        }
    }
}
