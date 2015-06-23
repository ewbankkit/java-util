package com.capitalone.cardcompanion.common.data.hazelcast;

import com.capitalone.cardcompanion.common.data.hazelcast.commands.AbstractHazelcastFindByValueCommandUnitTest;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AttributeType;
import com.hazelcast.query.impl.QueryException;
import com.hazelcast.query.impl.QueryableEntry;
import junitx.util.PrivateAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class IMapMock<K, V> implements ConcurrentMap<K, V>, IMap<K, V> {

    // some of the mocks used for testing have trouble with the delegate so just make this a java class

    private final ConcurrentMap<K,V> delegate = new ConcurrentHashMap<>();

    public IMapMock() {

    }

    @Override
    public boolean tryRemove(K k, long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public String getPartitionKey() {
        return null;
    }

    @Override
    public EntryView<K, V> getEntryView(K k) {
        return null;
    }

    @Override
    public void lock(K k, long l, TimeUnit timeUnit) {}

    @Override
    public LocalMapStats getLocalMapStats() {
        return null;
    }

    @Override
    public Object executeOnKey(K k, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map<K, Object> executeOnKeys(Set<K> set, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public void submitToKey(K k, EntryProcessor entryProcessor, ExecutionCallback executionCallback) {

    }

    @Override
    public Future submitToKey(K k, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map<K, Object> executeOnEntries(EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map<K, Object> executeOnEntries(EntryProcessor entryProcessor, Predicate predicate) {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet(Predicate predicate) {
        return null;
    }

    @Override
    public Collection<V> values(final Predicate predicate) {
        Collection<V> values = new ArrayList<>();
        for ( Map.Entry e : delegate.entrySet() ) {
            if ( predicate.apply(new StubQueryableEntry(e))) {
                values.add((V)e.getValue());
            }
        }
        return values;
    }

    @Override
    public Set<K> localKeySet() {
        return null;
    }

    @Override
    public Set<K> localKeySet(Predicate predicate) {
        return null;
    }

    @Override
    public void addIndex(String s, boolean b) {

    }

    @Override
    public Set<K> keySet(Predicate predicate) {
        return null;
    }

    @Override
    public boolean evict(Object o) {
        return false;
    }

    @Override
    public void unlock(K k) {

    }

    @Override
    public void forceUnlock(K k) {

    }

    @Override
    public String addInterceptor(MapInterceptor mapInterceptor) {
        return null;
    }

    @Override
    public boolean tryLock(K k, long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public boolean tryLock(K k) {
        return false;
    }

    @Override
    public boolean isLocked(K k) {
        return false;
    }

    @Override
    public void lock(K k) {

    }

    @Override
    public void flush() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<K, V> getAll(Set<K> ks) {
        return null;
    }

    @Override
    public Future<V> getAsync(K k) {
        return null;
    }

    @Override
    public Future<V> putAsync(K k, V v) {
        return null;
    }

    @Override
    public Future<V> removeAsync(K k) {
        return null;
    }

    @Override
    public boolean tryPut(K k, V v, long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public V put(K k, V v, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void putTransient(K k, V v, long l, TimeUnit timeUnit) {

    }

    @Override
    public void set(K k, V v) {

    }

    @Override
    public void set(K k, V v, long l, TimeUnit timeUnit) {

    }

    @Override
    public void destroy() {

    }

    @Override
    @Deprecated
    public Object getId() {
        return null;
    }

    @Override
    public V putIfAbsent(K k, V v, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<V> putAsync(K k, V v, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void delete(Object o) {

    }

    @Override
    public String addLocalEntryListener(EntryListener<K, V> entryListener) {
        return null;
    }

    @Override
    public String addLocalEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, K k, boolean b) {
        return null;
    }

    @Override
    public String addLocalEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, boolean b) {
        return null;
    }

    @Override
    public void removeInterceptor(String s) {

    }

    @Override
    public String addEntryListener(EntryListener<K, V> entryListener, boolean b) {
        return null;
    }

    @Override
    public boolean removeEntryListener(String s) {
        return false;
    }

    @Override
    public String addEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, boolean b) {
        return null;
    }

    @Override
    public String addEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, K k, boolean b) {
        return null;
    }

    @Override
    public String addEntryListener(EntryListener<K, V> entryListener, K k, boolean b) {
        return null;
    }

    public boolean containsKey(Object o) {
        return delegate.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return delegate.containsValue(o);
    }

    public V get(Object o) {
        return delegate.get(o);
    }

    public V put(K k, V v) {
        return delegate.put(k,v);
    }

    public V remove(Object o) {
        return delegate.remove(o);
    }

    public boolean remove(Object o, Object o1) {
        return delegate.remove(o,o1);
    }

    public V putIfAbsent(K k, V v) {
        return delegate.putIfAbsent(k,v);
    }

    public boolean replace(K k, V v, V v1) {
        return delegate.replace(k,v,v1);
    }

    public V replace(K k, V v) {
        return delegate.replace(k,v);
    }

    public Set<K> keySet() {
        return delegate.keySet();
    }

    public Collection<V> values() {
        return delegate.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    public void clear() {
        delegate.clear();
    }

    private static class StubQueryableEntry implements QueryableEntry {

        private final Map.Entry delegate;

        private StubQueryableEntry(Map.Entry delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object getValue() {
            return delegate.getValue();
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

        @Override
        public Object getKey() {
            return delegate.getKey();
        }

        @Override
        public Comparable getAttribute(String s) throws QueryException {
            try {
                return (Comparable) PrivateAccessor.getField(delegate.getValue(), s);
            }
            catch(NoSuchFieldException e) {
                throw new QueryException(e);
            }
        }

        @Override
        public AttributeType getAttributeType(String s) {
            return AttributeType.CHAR;
        }

        @Override
        public Data getKeyData() {
            return null;
        }

        @Override
        public Data getValueData() {
            return null;
        }

        @Override
        public Data getIndexKey() {
            return null;
        }
    }
}
