package io.netty.util.collection;

import io.netty.util.collection.CharObjectMap.PrimitiveEntry;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class CharObjectHashMap<V> implements CharObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private final Iterable<PrimitiveEntry<V>> entries;
    private final Set<Entry<Character, V>> entrySet;
    private final Set<Character> keySet;
    private char[] keys;
    private final float loadFactor;
    private int mask;
    private int maxSize;
    private int size;
    private V[] values;

    private final class EntrySet extends AbstractSet<Entry<Character, V>> {
        private EntrySet() {
        }

        public Iterator<Entry<Character, V>> iterator() {
            return new MapIterator();
        }

        public int size() {
            return CharObjectHashMap.this.size();
        }
    }

    private final class KeySet extends AbstractSet<Character> {
        private KeySet() {
        }

        public int size() {
            return CharObjectHashMap.this.size();
        }

        public boolean contains(Object o) {
            return CharObjectHashMap.this.containsKey(o);
        }

        public boolean remove(Object o) {
            return CharObjectHashMap.this.remove(o) != null;
        }

        public boolean retainAll(Collection<?> retainedKeys) {
            boolean changed = false;
            Iterator<PrimitiveEntry<V>> iter = CharObjectHashMap.this.entries().iterator();
            while (iter.hasNext()) {
                if (!retainedKeys.contains(Character.valueOf(((PrimitiveEntry) iter.next()).key()))) {
                    changed = true;
                    iter.remove();
                }
            }
            return changed;
        }

        public void clear() {
            CharObjectHashMap.this.clear();
        }

        public Iterator<Character> iterator() {
            return new Iterator<Character>() {
                private final Iterator<Entry<Character, V>> iter = CharObjectHashMap.this.entrySet.iterator();

                public boolean hasNext() {
                    return this.iter.hasNext();
                }

                public Character next() {
                    return (Character) ((Entry) this.iter.next()).getKey();
                }

                public void remove() {
                    this.iter.remove();
                }
            };
        }
    }

    final class MapEntry implements Entry<Character, V> {
        private final int entryIndex;

        MapEntry(int entryIndex) {
            this.entryIndex = entryIndex;
        }

        public Character getKey() {
            verifyExists();
            return Character.valueOf(CharObjectHashMap.this.keys[this.entryIndex]);
        }

        public V getValue() {
            verifyExists();
            return CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
        }

        public V setValue(V value) {
            verifyExists();
            V prevValue = CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
            CharObjectHashMap.this.values[this.entryIndex] = CharObjectHashMap.toInternal(value);
            return prevValue;
        }

        private void verifyExists() {
            if (CharObjectHashMap.this.values[this.entryIndex] == null) {
                throw new IllegalStateException("The map entry has been removed");
            }
        }
    }

    private final class MapIterator implements Iterator<Entry<Character, V>> {
        private final PrimitiveIterator iter;

        private MapIterator() {
            this.iter = new PrimitiveIterator();
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public Entry<Character, V> next() {
            if (hasNext()) {
                this.iter.next();
                return new MapEntry(this.iter.entryIndex);
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            this.iter.remove();
        }
    }

    private final class PrimitiveIterator implements PrimitiveEntry<V>, Iterator<PrimitiveEntry<V>> {
        private int entryIndex;
        private int nextIndex;
        private int prevIndex;

        private PrimitiveIterator() {
            this.prevIndex = -1;
            this.nextIndex = -1;
            this.entryIndex = -1;
        }

        private void scanNext() {
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i == CharObjectHashMap.this.values.length) {
                    return;
                }
            } while (CharObjectHashMap.this.values[this.nextIndex] == null);
        }

        public boolean hasNext() {
            if (this.nextIndex == -1) {
                scanNext();
            }
            return this.nextIndex < CharObjectHashMap.this.keys.length;
        }

        public PrimitiveEntry<V> next() {
            if (hasNext()) {
                this.prevIndex = this.nextIndex;
                scanNext();
                this.entryIndex = this.prevIndex;
                return this;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.prevIndex < 0) {
                throw new IllegalStateException("next must be called before each remove.");
            }
            if (CharObjectHashMap.this.removeAt(this.prevIndex)) {
                this.nextIndex = this.prevIndex;
            }
            this.prevIndex = -1;
        }

        public char key() {
            return CharObjectHashMap.this.keys[this.entryIndex];
        }

        public V value() {
            return CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
        }

        public void setValue(V value) {
            CharObjectHashMap.this.values[this.entryIndex] = CharObjectHashMap.toInternal(value);
        }
    }

    public CharObjectHashMap() {
        this(8, 0.5f);
    }

    public CharObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public CharObjectHashMap(int initialCapacity, float loadFactor) {
        this.keySet = new KeySet();
        this.entrySet = new EntrySet();
        this.entries = new Iterable<PrimitiveEntry<V>>() {
            public Iterator<PrimitiveEntry<V>> iterator() {
                return new PrimitiveIterator();
            }
        };
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
        this.mask = capacity - 1;
        this.keys = new char[capacity];
        this.values = (Object[]) new Object[capacity];
        this.maxSize = calcMaxSize(capacity);
    }

    private static <T> T toExternal(T value) {
        return value == NULL_VALUE ? null : value;
    }

    private static <T> T toInternal(T value) {
        return value == null ? NULL_VALUE : value;
    }

    public V get(char key) {
        int index = indexOf(key);
        return index == -1 ? null : toExternal(this.values[index]);
    }

    public V put(char key, V value) {
        int startIndex = hashIndex(key);
        int index = startIndex;
        while (this.values[index] != null) {
            if (this.keys[index] == key) {
                V previousValue = this.values[index];
                this.values[index] = toInternal(value);
                return toExternal(previousValue);
            }
            index = probeNext(index);
            if (index == startIndex) {
                throw new IllegalStateException("Unable to insert");
            }
        }
        this.keys[index] = key;
        this.values[index] = toInternal(value);
        growSize();
        return null;
    }

    public void putAll(Map<? extends Character, ? extends V> sourceMap) {
        if (sourceMap instanceof CharObjectHashMap) {
            CharObjectHashMap<V> source = (CharObjectHashMap) sourceMap;
            for (int i = 0; i < source.values.length; i++) {
                Object sourceValue = source.values[i];
                if (sourceValue != null) {
                    put(source.keys[i], sourceValue);
                }
            }
            return;
        }
        for (Entry<? extends Character, ? extends V> entry : sourceMap.entrySet()) {
            put((Character) entry.getKey(), entry.getValue());
        }
    }

    public V remove(char key) {
        int index = indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        removeAt(index);
        return toExternal(prev);
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        Arrays.fill(this.keys, '\u0000');
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    public boolean containsKey(char key) {
        return indexOf(key) >= 0;
    }

    public boolean containsValue(Object value) {
        V v1 = toInternal(value);
        for (V v2 : this.values) {
            if (v2 != null && v2.equals(v1)) {
                return true;
            }
        }
        return false;
    }

    public Iterable<PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    public Collection<V> values() {
        return new AbstractCollection<V>() {
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    final PrimitiveIterator iter = new PrimitiveIterator();

                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    public V next() {
                        return this.iter.next().value();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public int size() {
                return CharObjectHashMap.this.size;
            }
        };
    }

    public int hashCode() {
        int hash = this.size;
        for (char key : this.keys) {
            hash ^= hashCode(key);
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharObjectMap)) {
            return false;
        }
        CharObjectMap other = (CharObjectMap) obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; i++) {
            V value = this.values[i];
            if (value != null) {
                Object otherValue = other.get(this.keys[i]);
                if (value == NULL_VALUE) {
                    if (otherValue != null) {
                        return false;
                    }
                } else if (!value.equals(otherValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean containsKey(Object key) {
        return containsKey(objectToKey(key));
    }

    public V get(Object key) {
        return get(objectToKey(key));
    }

    public V put(Character key, V value) {
        return put(objectToKey(key), (Object) value);
    }

    public V remove(Object key) {
        return remove(objectToKey(key));
    }

    public Set<Character> keySet() {
        return this.keySet;
    }

    public Set<Entry<Character, V>> entrySet() {
        return this.entrySet;
    }

    private char objectToKey(Object key) {
        return ((Character) key).charValue();
    }

    private int indexOf(char key) {
        int startIndex = hashIndex(key);
        int index = startIndex;
        while (this.values[index] != null) {
            if (key == this.keys[index]) {
                return index;
            }
            index = probeNext(index);
            if (index == startIndex) {
                return -1;
            }
        }
        return -1;
    }

    private int hashIndex(char key) {
        return hashCode(key) & this.mask;
    }

    private static int hashCode(char key) {
        return key;
    }

    private int probeNext(int index) {
        return (index + 1) & this.mask;
    }

    private void growSize() {
        this.size++;
        if (this.size <= this.maxSize) {
            return;
        }
        if (this.keys.length == Integer.MAX_VALUE) {
            throw new IllegalStateException("Max capacity reached at size=" + this.size);
        }
        rehash(this.keys.length << 1);
    }

    private boolean removeAt(int index) {
        this.size--;
        this.keys[index] = '\u0000';
        this.values[index] = null;
        boolean movedBack = false;
        int nextFree = index;
        int i = probeNext(index);
        while (this.values[i] != null) {
            int bucket = hashIndex(this.keys[i]);
            if ((i < bucket && (bucket <= nextFree || nextFree <= i)) || (bucket <= nextFree && nextFree <= i)) {
                this.keys[nextFree] = this.keys[i];
                this.values[nextFree] = this.values[i];
                movedBack = true;
                this.keys[i] = '\u0000';
                this.values[i] = null;
                nextFree = i;
            }
            i = probeNext(i);
        }
        return movedBack;
    }

    private int calcMaxSize(int capacity) {
        return Math.min(capacity - 1, (int) (((float) capacity) * this.loadFactor));
    }

    private void rehash(int newCapacity) {
        char[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new char[newCapacity];
        this.values = (Object[]) new Object[newCapacity];
        this.maxSize = calcMaxSize(newCapacity);
        this.mask = newCapacity - 1;
        for (int i = 0; i < oldVals.length; i++) {
            V oldVal = oldVals[i];
            if (oldVal != null) {
                char oldKey = oldKeys[i];
                int index = hashIndex(oldKey);
                while (this.values[index] != null) {
                    index = probeNext(index);
                }
                this.keys[index] = oldKey;
                this.values[index] = oldVal;
            }
        }
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.size * 4);
        sb.append('{');
        boolean first = true;
        for (int i = 0; i < this.values.length; i++) {
            V value = this.values[i];
            if (value != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(keyToString(this.keys[i])).append('=').append(value == this ? "(this Map)" : toExternal(value));
                first = false;
            }
        }
        return sb.append('}').toString();
    }

    protected String keyToString(char key) {
        return Character.toString(key);
    }
}
