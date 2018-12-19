package org.spbstu.chernonog.allthewaynew;

import kotlin.Pair;

public class SplayTree {
    static final class Node {
        int key;
        Node left;
        Node right;
        Node parent;
        String value;

        Node(int key, String value, Node left, Node right, Node parent) {
            this.key = key;
            this.right = right;
            this.left = left;
            this.parent = parent;
            this.value = value;
        }

        Node(int key, String value) {
            new Node(key, value, null, null, null);
        }

        @Override
        public String toString() {
            return "{" + key + "=" + value + "}";
        }

    }


    private Node root;

    int size = 0;

    //SplayTree methods

    private void setParent(Node child, Node parent) {
        if (child != null) {
            child.parent = parent;
        }
    }

    private void saveParent(Node node) {
        setParent(node.left, node);
        setParent(node.right, node);
    }

    private void rotate(Node parent, Node child) {
        Node grandparent = parent.parent;
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


    private Node splay(Node node) {
        if (node.parent == null) {
            return node;
        }

        Node parent = node.parent;
        Node grandparent = parent.parent;

        if (grandparent == null) {
            rotate(parent, node);
            return node;
        }

        if ((grandparent.left == parent) == (grandparent.right == parent)) {
            rotate(grandparent, parent);
            rotate(parent, node);
        } else {
            rotate(parent, node);
            rotate(grandparent, node);
        }
        return splay(node);
    }

    private Node find(Node node, int key) {
        if (node == null) {
            return null;
        }
        if (node.key == key) {
            return splay(node);
        }
        if (key > node.key && node.right != null) {
            return find(node.right, key);
        }
        if (key < node.key && node.left != null) {
            return find(node.left, key);
        }
        return splay(node);
    }


    private Pair<Node, Node> split(Node start, int key) {
        if (start == null) {
            return new Pair<>(null, null);
        }
        start = find(start, key);
        if (start.key == key) {
            setParent(start.right, null);
            setParent(start.left, null);
            return new Pair<>(start.left, start.right);
        }
        if (start.key < key) {
            Node right = start.right;
            start.right = null;
            setParent(right, null);
            return new Pair<>(start, right);
        } else {
            Node left = start.left;
            start.left = null;
            setParent(left, null);
            return new Pair<>(left, start);
        }
    }

    private Node insert(Node start, int key, String value) {
        Pair<Node, Node> pair = split(start, key);
        start = new Node(key, value, pair.getFirst(), pair.getSecond(), null);
        saveParent(start);
        return start;
    }

    private Node merge(Node left, Node right) {
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }
        right = find(right, left.key);
        right.left = left;
        left.parent = right;
        return right;
    }

    private Pair<String, Node> del(Node start, int key) {
        start = find(start, key);
        String res = null;
        if (start.key != key) {
            return new Pair<>(res, start);
        } else {
            res = start.value;
            setParent(start.left, null);
            setParent(start.right, null);
            return new Pair<>(res, merge(start.left, start.right));
        }
    }

    private void deleteEntry(){

    }

    //utils for Map methods

    private Node getFirstEntry() {
        Node p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    private Node getLastEntry() {
        Node p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    private Node successor(Node t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            Node p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Node p = t.parent;
            Node ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }



    //public methods


    public String first(){
        return getFirstEntry().value;
    }

    public String last(){
        return getLastEntry().value;
    }

    public boolean containsKey(int key) {
        root = find(root, key);
        return root.key == key;
    }

    public boolean containsValue(Object value) {
        for (Node e = getFirstEntry(); e != null; e = successor(e))
            if (value.equals(e.value))
                return true;
        return false;
    }

    public String put(String value, int key) {
        root = insert(root, key, value);
        return root.value;
    }

    public String remove(int key) {
        Pair<String, Node> pair = del(root, key);
        root = pair.getSecond();
        return pair.getFirst();
    }

    public String get(int key) {
        root = find(root, key);
        if (root.key != key) {
            return null;
        }
        return root.value;
    }

    public Node getRoot() {
        return root;
    }

    public String replace(int key, String value) {
        root = find(root, key);
        if (root.key == key) {
            String oldValue = root.value;
            root.value = value;
            return oldValue;
        }
        return null;
    }

    public boolean isEmpty() {
        return root == null;
    }


}
