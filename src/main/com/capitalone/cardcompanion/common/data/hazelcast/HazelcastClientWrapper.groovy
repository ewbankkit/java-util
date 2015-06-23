//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast

import com.capitalone.cardcompanion.common.Config
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.client.config.ClientNetworkConfig
import com.hazelcast.core.DistributedObject
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap
import groovy.util.logging.Slf4j

/**
 * A wrapper around a HazelcastClient that ensures the client connection is still alive and restarts it if it is not.
 * If it loses complete communication with the Hazelcast cluster, the client could shutdown. This shouldn't
 * happen too frequently, but this class will restart the cache.
 */
@Slf4j
final class HazelcastClientWrapper {

    public static final String USER_MAP_NAME = 'user' // Map name in Hazelcast configuration

    /**
     * The underlying Hazelcast client. There is only a single instance of
     * this that is shared by all connections to the cache. If the connection dies, this will be
     * reinitialized with a new connection.
     */
    @Delegate
    private volatile HazelcastInstance           hazelcastClient

    private final    Timer                     hazelcastWatchdogTimer = new Timer('Hazelcast-Client-Watchdog', true)

    /** maintains a cache of map names to the maps so hazelcast doesn't need to load the map proxy each time */
    private final    LoadingCache<String,IMap> mapCache               = CacheBuilder.newBuilder().build(new CacheLoader<String, IMap>() {
        @Override
        IMap load(String name) throws Exception {
            hazelcastClient.getMap(name)
        }
    })

    private HazelcastClientWrapper() {}

    static HazelcastClientWrapper getInstance() {
        LazyHolder.INSTANCE
    }

    /**
     * Returns whether or not Hazelcast is enabled.
     */
    public static boolean isHazelcastEnabled() {
        Config.instance.getBoolean('hazelcast.enabled', false)
    }

    // This overrides getMap from HazelcastInstance but because of how groovy does delegation, we can't use the @Override annotation here.
    public <K, V> IMap<K, V> getMap(String name) {
        mapCache.get(name)
    }

    // there seems to be a groovy issue that getLock and getDistributedObject are not picked up from the delegate
    @Override
    ILock getLock(Object o) {
        return hazelcastClient.getLock(o)
    }

    @Override
    def <T extends DistributedObject> T getDistributedObject(String s, Object o) {
        return hazelcastClient.getDistributedObject(s,o)
    }

    private void startHazelcastClientWatchdogTimer() {
        // Use a daemon timer so this doesn't keep the app running on shutdown.
        hazelcastWatchdogTimer.schedule(
            new TimerTask() {
                @Override
                void run() {
                    if (!hazelcastClient || !hazelcastClient.getLifecycleService().isRunning()) {
                        log.info 'Creating new Hazelcast client'
                        try {
                            // New connection, clear the cache. even if a client gets a map from here before it is cleared,
                            // it will throw an exception and Hystrix will fall back to hitting the database directly.
                            mapCache.invalidateAll()
                            hazelcastClient?.shutdown()
                            hazelcastClient = createHazelcastClient()
                            preloadMaps()
                        }
                        catch (Exception ex) {
                            log.error('Error connecting to Hazelcast', ex)
                        }
                    }
                }

                private HazelcastInstance createHazelcastClient() {
                    ClientConfig clientConfig = new ClientConfig()
                    ClientNetworkConfig networkConfig = clientConfig.networkConfig
                    List<String> seedAddresses = Config.instance.getList('hazelcast.addresses').get()
                    seedAddresses.each { networkConfig.addAddress(it) }

                    clientConfig.groupConfig.name = HazelcastUtils.getHazelcastClusterName(Config.environmentName)
                    return HazelcastClient.newHazelcastClient(clientConfig)
                }

                /**
                 * Pre-loads the maps used by Hazelcast.
                 */
                private void preloadMaps() {
                    mapCache.refresh(USER_MAP_NAME)
                }
            },
            Config.instance.getLong('hazelcast.watchdog.initialDelayMillis'  ,  30000L),
            Config.instance.getLong('hazelcast.watchdog.checkFrequencyMillis', 300000L)
        )
    }

    public void start() {
        startHazelcastClientWatchdogTimer()
    }

    public void stop() {
        hazelcastWatchdogTimer.cancel()
        hazelcastClient?.shutdown()
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final HazelcastClientWrapper INSTANCE = new HazelcastClientWrapper()
    }
}
