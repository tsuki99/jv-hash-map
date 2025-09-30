package core.basesyntax;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private int currentCapacity = INITIAL_CAPACITY;
    private float threshold = currentCapacity * LOAD_FACTOR;
    private int size;
    private Node<K, V>[] table;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = (Node<K,V>[])new Node[INITIAL_CAPACITY];
    }

    @Override
    public void put(K key, V value) {
        if (size >= threshold) {
            resize();
        }

        int index = getIndex(key);
        Node<K,V> currentNode = table[index];
        Node<K, V> lastNode = null;

        while (currentNode != null) {
            lastNode = currentNode;
            if (currentNode.key == key || currentNode.key != null && currentNode.key.equals(key)) {
                currentNode.value = value;
                return;
            }
            currentNode = currentNode.next;
        }

        Node<K, V> newNode = new Node<>(hash(key), key, value, null);

        if (lastNode == null) {
            table[index] = newNode;
        } else {
            lastNode.next = newNode;
        }
        size++;
    }

    @Override
    public V getValue(K key) {
        int index = getIndex(key);
        Node<K,V> currentNode = table[index];

        while (currentNode != null) {
            if (key == currentNode.key
                    || currentNode.key != null && currentNode.key.equals(key)) {
                return currentNode.value;
            }
            currentNode = currentNode.next;
        }

        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    private int getIndex(K key) {
        int h = hash(key) == Integer.MIN_VALUE ? 0 : Math.abs(hash(key));
        return h % currentCapacity;
    }

    private int hash(K key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private void resize() {
        currentCapacity = currentCapacity * 2;
        threshold = currentCapacity * LOAD_FACTOR;
        transfer();
    }

    private void transfer() {
        Node<K, V>[] newSizeTable = new Node[currentCapacity];

        for (int i = 0; i < table.length; i++) {
            Node<K, V> currentNode = table[i];
            while (currentNode != null) {
                Node<K, V> nextNode = currentNode.next;
                currentNode.next = null;

                int indexForNewTable = getIndex(currentNode.key);
                insertNodeAtNewTable(currentNode, newSizeTable, indexForNewTable);

                currentNode = nextNode;
            }
        }
        table = newSizeTable;
    }

    private void insertNodeAtNewTable(Node<K, V> node,
                                      Node<K, V>[] newTable, int indexForNewTable) {
        if (newTable[indexForNewTable] == null) {
            newTable[indexForNewTable] = node;
            return;
        }

        Node<K, V> currentNode = newTable[indexForNewTable];
        while (currentNode.next != null) {
            currentNode = currentNode.next;
        }
        currentNode.next = node;
    }

    private static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
