package org.spbstu.chernonog.project;


import java.util.*;

public class SplayTreeSet<E> extends AbstractSet<E>
        implements SortedSet<E> {


    //сравнение элементов
    private int compare(Object first, E second) {
        if (comparator == null) {
            Comparable<? super E> f = (Comparable<? super E>) first;
            return f.compareTo(second);
        } else {
            E f = (E) first;
            return comparator.compare(f, second);
        }
    }

    //установка родителя
    private void setParent(Node<E> child, Node<E> parent) {
        if (child != null) {
            child.parent = parent;
        }
    }

    //передача ссылки на родителя детям данной вершины
    private void saveParent(Node<E> node) {
        setParent(node.left, node);
        setParent(node.right, node);
    }

    //поворот ребенка и родителя
    private void rotate(Node<E> parent, Node<E> child) {
        Node<E> grandparent = parent.parent;
        if (grandparent != null) {   //обновление ссылок дедушки, если он имелся
            if (parent == grandparent.left) {
                grandparent.left = child;
            } else {
                grandparent.right = child;
            }
        }// повороты
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

    // механизм "вытягивания" вершины к корню
    private Node<E> splay(Node<E> entry) {
        if (entry.parent == null) {
            return entry;
        }
        Node<E> parent = entry.parent;
        Node<E> grandparent = parent.parent;
        if (grandparent == null) {  //если вершина - ребенок корня
            rotate(parent, entry);
            return entry;
        }
        if ((grandparent.left == parent) == (parent.left == entry)) { //zig-zig случай
            rotate(grandparent, parent);
            rotate(parent, entry);
        } else { //zig-zag случай
            rotate(parent, entry);
            rotate(grandparent, entry);
        }
        return splay(entry);
    }

    // стандартный двоичный поиск
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

    // вставка элемента, использующая splay
    private boolean splayInsert(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (root == null) {
            root = new Node<>(element, null, null, null);
            size++;
            return true;
        }
        Node<E> node = splay(find(root, element));// поиск и вытягивание ближайшего к найденному элемента
        int cmp = compare(node.element, element);
        if (cmp == 0) { // если элемент уже имеется в дереве
            return false;
        } else if (cmp < 0) { // если ближайший элемент меньше
            Node<E> right = node.right;
            node.right = null;
            root = new Node<>(element, null, node, right);
            saveParent(root);
        } else { // если ближайший элемент больше
            Node<E> left = node.left;
            node.left = null;
            root = new Node<>(element, null, left, node);
            saveParent(root);
        }
        size++;
        return true;
    }

    //"слияние" двух деревьев в одно, при условии, что любой элемент правого больше любого элемента левого
    private Node<E> merge(Node<E> left, Node<E> right) {
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }
        //если оба дерева не null, то в корень правого вытягивается ближайший к левому ключ
        right.parent = null; //так как splay остановится только, если parent == null
        right = splay(find(right, left.element));
        // корнем нового дерева становится вытянутая вершина
        right.left = left;
        left.parent = right;
        return right;
    }

    // удаление для пользователя
    private boolean splayDelete(E element) {
        Node<E> node = splay(find(root, element));
        int cmp = compare(element, node.element);
        if (cmp != 0) {
            root = node;
            return false;
        } else { // если элемент присутствует в дереве, вершина с ним удаляется, а корнем становится результат слияния детей
            setParent(node.left, null);
            setParent(node.right, null);
            root = merge(node.left, node.right);
            size--;
            return true;
        }
    }

    //получение вершины с наименьшим элементом
    private Node<E> getFirstNode() {
        Node<E> node = root;
        if (node != null)
            while (node.left != null)
                node = node.left;
        return node;
    }

    //получение вершины с наибольшим элементом
    private Node<E> getLastNode() {
        Node<E> node = root;
        if (node != null)
            while (node.right != null)
                node = node.right;
        return node;
    }

    // добавление элемента без перестройки дерева (для работы с коллекциями)
    private boolean nonSplayInsert(E element) {
        if (root == null) {
            root = new Node<E>(element, null, null, null);
            return true;
        }
        Node<E> closest = find(root, element);
        int cmp = compare(element, closest.element);
        if (cmp == 0) {
            return false;
        }
        Node<E> node = new Node<>(element, closest, null, null);
        if (cmp < 0) {
            closest.left = node;
        } else {
            closest.right = node;
        }
        size++;
        return true;
    }

    // удаление без полной перестройки дерева
    private void iteratorDelete(Node<E> node) {
        Node<E> replacement = merge(node.left, node.right); // замена удаленной вершины из слияния ее детей
        Node<E> parent = node.parent;
        setParent(replacement, parent);
        if (parent != null) { // обновление ссылок родителя, если он был
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

    // получение следующей по порядку следования элементов вершины
    // используется в итераторе, а так же в subset
    private Node<E> successor(Node<E> current) {
        if (current == null)
            return null;
        else if (current.right != null) {
            Node<E> next = current.right;
            while (next.left != null)
                next = next.left;
            return next;
        } else {
            Node<E> parent = current.parent;
            Node<E> child = current;
            while (parent != null && child == parent.right) {
                child = parent;
                parent = parent.parent;
            }
            return parent;
        }
    }

    // получение предыдущей по порядку следования элементов вершины
    // используется в subset
    private Node<E> predecessor(Node<E> current) {
        if (current == null)
            return null;
        else if (current.left != null) {
            Node<E> prev = current.left;
            while (prev.right != null)
                prev = prev.right;
            return prev;
        } else {
            Node<E> parent = current.parent;
            Node<E> child = current;
            while (parent != null && child == parent.left) {
                child = parent;
                parent = parent.parent;
            }
            return parent;
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


    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException();
        }
        return new SubSet(this, fromElement, toElement);
    }


    @Override
    public SortedSet<E> headSet(E toElement) {
        if (toElement == null) {
            throw new NullPointerException();
        }
        return new SubSet(this, null, toElement);
    }


    @Override
    public SortedSet<E> tailSet(E fromElement) {
        if (fromElement == null) {
            throw new NullPointerException();
        }
        return new SubSet(this, fromElement, null);
    }

    @Override
    public E first() {
        Node<E> node = getFirstNode();
        if (node == null) {
            throw new NoSuchElementException();
        }
        return node.element;
    }

    @Override
    public E last() {
        Node<E> node = getLastNode();
        if (node == null) {
            throw new NoSuchElementException();
        }
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
        if (key == null){
            throw new NullPointerException();
        }
        if (root == null) {
            return false;
        }
        root = splay(find(root, k));
        return compare(root.element, k) == 0;
    }

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
        return splayDelete(element);
    }

    // во всех All-операциях splay не выполняется ради эффективности
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            Node<E> node = find(root, (E) element);
            if (compare(element, node.element) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (nonSplayInsert(e)) {
                modified = true;
            }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
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
    public boolean removeAll(Collection<?> c) {
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

    //класс вершины
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

        public int hashCode() {
            int valueHash = (element == null ? 0 : element.hashCode());
            return valueHash * 31;
        }

        public String toString() {
            return element.toString();
        }
    }


    // итератор главного дерева
    // в соответствие с контрактом SortedSet возвращает элементы в порядке их следования
    class ElementsIterator implements Iterator<E> {
        Node<E> next;
        Node<E> lastReturned;

        ElementsIterator(Node<E> first) {
            lastReturned = null;
            next = first;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        private Node<E> nextNode() {
            Node<E> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            next = successor(e);
            lastReturned = e;
            return e;
        }

        @Override
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

    // итератор любого subset
    class SubIterator extends ElementsIterator
            implements Iterator<E> {

        Node<E> limit;

        SubIterator(Node<E> first, Node<E> last) {
            super(first);
            this.limit = successor(last);
        }

        @Override
        public boolean hasNext() {
            return (next != null && next != limit);
        }
    }

    class SubSet extends AbstractSet<E>
            implements SortedSet<E> {

        SplayTreeSet<E> motherSet; // set - родитель
        // нижняя и верхняя границы. если одна из границ null, subset не ограничен сверху или снизу соответственно
        E low;
        E high;

        // проверка вхождения элемента в границы сета
        private boolean inRange(E element) {
            if (low != null && high != null) {
                return (motherSet.compare(element, low) >= 0 && motherSet.compare(element, high) < 0);
            }
            if (low == null) {
                return (motherSet.compare(element, high) < 0);
            }
            return (motherSet.compare(element, low) >= 0);
        }

        SubSet(SplayTreeSet<E> motherSet, E low, E high) {
            this.motherSet = motherSet;
            this.low = low;
            this.high = high;
        }


        @Override
        public Comparator<? super E> comparator() {
            return motherSet.comparator();
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            if (!inRange(fromElement) || !inRange(toElement)) {
                throw new IllegalArgumentException();
            }
            return motherSet.subSet(fromElement, toElement);
        }


        @Override
        public SortedSet<E> headSet(E toElement) {
            if (!inRange(toElement)) {
                throw new IllegalArgumentException();
            }
            return motherSet.subSet(this.low, toElement);
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            if (!inRange(fromElement)) {
                throw new NullPointerException();
            }
            return motherSet.subSet(fromElement, this.high);
        }

        //получение вершины с наименьшим элементом
        private Node<E> getFirstNode() {
            if (low == null) return motherSet.getFirstNode();
            Node<E> node = motherSet.find(motherSet.root, low);//поиск элемента, ближайшего к нижней границе
            if (node == null) {
                return node;
            }
            while (!inRange(node.element)) { //если найденный элемент не входит в границы
                node = motherSet.successor(node);
                if (node == null) {
                    break;
                }
            }
            return node;
        }

        //получение вершины с наимбольшим элементом
        private Node<E> getLastNode() {
            if (high == null) return motherSet.getLastNode();
            Node<E> node = motherSet.find(motherSet.root, high);//поиск элемента, ближайщего к верхней границе
            if (node == null) {
                return node;
            }
            while (!inRange(node.element)) {//если найденный элемент не входит в границы
                node = motherSet.predecessor(node);
                if (node == null) {
                    break;
                }
            }
            return node;
        }

        @Override
        public E first() {
            Node<E> node = getFirstNode();
            if (node == null) {
                throw new NoSuchElementException();
            }
            return node.element;
        }

        @Override
        public E last() {
            Node<E> node = getLastNode();
            if (node == null) {
                throw new NoSuchElementException();
            }
            return node.element;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            E element = (E) o;
            if (!inRange(element)) {
                return false;
            }
            return motherSet.contains(element);
        }

        @Override
        public Iterator<E> iterator() {
            return new SubIterator(getFirstNode(), getLastNode());
        }

        @Override
        public boolean add(E e) {
            if (!inRange(e)) {
                throw new IllegalArgumentException();
            }
            return motherSet.add(e);
        }


        @Override
        public boolean remove(Object o) {
            E element = (E) o;
            if (!inRange(element)) {
                throw new IllegalArgumentException();
            }
            return motherSet.remove(element);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
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
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                E next = (E) i.next();
                if (!inRange(next)) {
                    throw new IllegalArgumentException();
                }
                if (!c.contains(next)) {
                    i.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                E next = (E) i.next();
                if (!inRange(next)) {
                    throw new IllegalArgumentException();
                }
                if (c.contains(next)) {
                    i.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public int size() {
            int res = 0;
            for (Iterator i = iterator(); i.hasNext(); i.next()) {
                res++;
            }
            return res;
        }

        // clear в subset удаляет все свои элементы, а так же все соответствующие элементы в родительском сете
        @Override
        public void clear() {
            Iterator<E> iterator = iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }
}
