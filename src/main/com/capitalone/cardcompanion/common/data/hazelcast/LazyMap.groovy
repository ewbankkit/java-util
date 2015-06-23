//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast

import com.hazelcast.core.EntryListener
import com.hazelcast.core.EntryView
import com.hazelcast.core.ExecutionCallback
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.map.EntryProcessor
import com.hazelcast.map.MapInterceptor
import com.hazelcast.monitor.LocalMapStats
import com.hazelcast.query.Predicate

import java.util.Map.Entry
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Implements the IMap interface but lazily retrieves the IMap instance. In the event that
 * hazelcast has not been initialized when this is called, it will throw an exception inside
 * of the hystrix command, which will fallback to using the database directly.
 *
 * The getMap call this class makes is threadsafe and only loads the map if it has not already
 * been loaded.
 *
 * @see HazelcastClientWrapper
 *
 * @param < K >
 * @param < V >
 */
class LazyMap<K, V> implements IMap<K, V> {
    private final HazelcastInstance hazelcastInstance
    private final String            mapName

    LazyMap(HazelcastInstance hazelcastInstance, String mapName) {
        this.hazelcastInstance = hazelcastInstance
        this.mapName = mapName
    }

    private IMap<K, V> getMap() {
        hazelcastInstance.getMap(mapName)
    }

    @Override
    int size() {
        getMap().size()
    }

    @Override
    boolean isEmpty() {
        getMap().isEmpty()
    }

    @Override
    boolean containsKey(Object o) {
        getMap().containsKey(o)
    }

    @Override
    boolean containsValue(Object o) {
        getMap().containsValue(o)
    }

    @Override
    V get(Object o) {
        getMap().get(o)
    }

    @Override
    V put(K k, V v) {
        getMap().put(k, v)
    }

    @Override
    V remove(Object o) {
        getMap().remove(o)
    }

    @Override
    void putAll(Map<? extends K, ? extends V> m) {
        getMap().putAll(m)
    }

    @Override
    void clear() {
        getMap().clear()
    }

    @Override
    boolean remove(Object o, Object o2) {
        getMap().remove(o,o2)
    }

    @Override
    void delete(Object o) {
        getMap().delete(o)
    }

    @Override
    void flush() {
        getMap().flush()
    }

    @Override
    String getName() {
        getMap().getName()
    }

    @Override
    String getServiceName() {
        getMap().getServiceName()
    }

    @Override
    Map<K, V> getAll(Set<K> ks) {
        getMap().getAll(ks)
    }

    @Override
    Future<V> getAsync(K k) {
        getMap().getAsync(k)
    }

    @Override
    Future<V> putAsync(K k, V v) {
        getMap().putAsync(k, v)
    }

    @Override
    Future<V> putAsync(K k, V v, long l, TimeUnit timeUnit) {
        getMap().putAsync(k,v,l,timeUnit)
    }

    @Override
    Future<V> removeAsync(K k) {
        getMap().removeAsync(k)
    }

    @Override
    boolean tryRemove(K k, long l, TimeUnit timeUnit) throws TimeoutException {
        getMap().tryRemove(k, l, timeUnit)
    }

    @Override
    boolean tryPut(K k, V v, long l, TimeUnit timeUnit) {
        getMap().tryPut(k, v, l, timeUnit)
    }

    @Override
    V put(K k, V v, long l, TimeUnit timeUnit) {
        getMap().put(k, v, l, timeUnit)
    }

    @Override
    void putTransient(K k, V v, long l, TimeUnit timeUnit) {
        getMap().putTransient(k, v, l, timeUnit)
    }

    @Override
    V putIfAbsent(K k, V v) {
        getMap().putIfAbsent(k, v)
    }

    @Override
    V putIfAbsent(K k, V v, long l, TimeUnit timeUnit) {
        getMap().putIfAbsent(k, v, l, timeUnit)
    }

    @Override
    boolean replace(K k, V v, V v2) {
        getMap().replace(k, v, v2)
    }

    @Override
    V replace(K k, V v) {
        getMap().replace(k, v)
    }

    @Override
    void set(K k, V v) {
        getMap().set(k, v)
    }

    @Override
    void set(K k, V v, long l, TimeUnit timeUnit) {
        getMap().set(k, v, l, timeUnit)
    }

    @Override
    void lock(K k) {
        getMap().lock(k)
    }

    @Override
    void lock(K k, long l, TimeUnit timeUnit) {
        getMap().lock(k,l,timeUnit)
    }

    @Override
    boolean isLocked(K k) {
        getMap().isLocked(k)
    }

    @Override
    boolean tryLock(K k) {
        getMap().tryLock(k)
    }

    @Override
    boolean tryLock(K k, long l, TimeUnit timeUnit) {
        getMap().tryLock(k, l, timeUnit)
    }

    @Override
    void unlock(K k) {
        getMap().unlock(k)
    }

    @Override
    void forceUnlock(K k) {
        getMap().forceUnlock(k)
    }

    @Override
    String addLocalEntryListener(EntryListener<K, V> kvEntryListener) {
        getMap().addLocalEntryListener(kvEntryListener)
    }

    @Override
    String addLocalEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, boolean b) {
        getMap().addLocalEntryListener(entryListener,predicate,b)
    }

    @Override
    String addLocalEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, K k, boolean b) {
        getMap().addLocalEntryListener(entryListener,predicate,k,b)
    }

    @Override
    String addInterceptor(MapInterceptor mapInterceptor) {
        getMap().addInterceptor(mapInterceptor)
    }

    @Override
    void removeInterceptor(String s) {
        getMap().removeInterceptor(s)
    }

    @Override
    String addEntryListener(EntryListener<K, V> entryListener, boolean b) {
        getMap().addEntryListener(entryListener, b)
    }

    @Override
    boolean removeEntryListener(String s) {
        getMap().removeEntryListener(s)
    }

    @Override
    String addEntryListener(EntryListener<K, V> entryListener, K k, boolean b) {
        getMap().addEntryListener(entryListener, k, b)
    }

    @Override
    String addEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, boolean b) {
        getMap().addEntryListener(entryListener,predicate,b)
    }

    @Override
    String addEntryListener(EntryListener<K, V> entryListener, Predicate<K, V> predicate, K k, boolean b) {
        getMap().addEntryListener(entryListener,predicate,k,b)
    }

    @Override
    EntryView<K, V> getEntryView(K k) {
        getMap().getEntryView(k)
    }

    @Override
    boolean evict(K k) {
        getMap().evict(k)
    }

    @Override
    Set<K> keySet() {
        getMap().keySet()
    }

    @Override
    Collection<V> values() {
        getMap().values()
    }

    @Override
    Set<Entry<K, V>> entrySet() {
        getMap().entrySet()
    }

    @Override
    Set<K> keySet(Predicate predicate) {
        getMap().keySet(predicate)
    }

    @Override
    Set<Entry<K, V>> entrySet(Predicate predicate) {
        getMap().entrySet(predicate)
    }

    @Override
    Collection<V> values(Predicate predicate) {
        getMap().values(predicate)
    }

    @Override
    Set<K> localKeySet() {
        getMap().localKeySet()
    }

    @Override
    Set<K> localKeySet(Predicate predicate) {
        getMap().localKeySet(predicate)
    }

    @Override
    void addIndex(String s, boolean b) {
        getMap().addIndex(s,b)
    }

    @Override
    LocalMapStats getLocalMapStats() {
        getMap().getLocalMapStats()
    }

    @Override
    Object executeOnKey(K k, EntryProcessor entryProcessor) {
        getMap().executeOnKey(k, entryProcessor)
    }

    @Override
    Map<K, Object> executeOnKeys(Set<K> set, EntryProcessor entryProcessor) {
        getMap().executeOnKeys(set, entryProcessor)
    }

    @Override
    void submitToKey(K k, EntryProcessor entryProcessor, ExecutionCallback executionCallback) {
        getMap().submitToKey(k, entryProcessor, executionCallback)
    }

    @Override
    Future submitToKey(K k, EntryProcessor entryProcessor) {
        getMap().submitToKey(k,entryProcessor)
    }

    @Override
    Map<K, Object> executeOnEntries(EntryProcessor entryProcessor) {
        getMap().executeOnEntries(entryProcessor)
    }

    @Override
    Map<K, Object> executeOnEntries(EntryProcessor entryProcessor, Predicate predicate) {
        getMap().executeOnEntries(entryProcessor,predicate)
    }

    @Override
    void destroy() {
        getMap().destroy()
    }

    @Override
    @Deprecated
    Object getId() {
        //noinspection GrDeprecatedAPIUsage
        getMap().getId()
    }

    @Override
    String getPartitionKey() {
        getMap().getPartitionKey()
    }
}
