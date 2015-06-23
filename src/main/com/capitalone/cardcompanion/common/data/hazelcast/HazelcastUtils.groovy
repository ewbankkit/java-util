//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast

class HazelcastUtils {

    private HazelcastUtils() {}

    /**
     * Gets the name of the hazelcast cluster for the given environment
     * @param env
     * @return
     */
    public static String getHazelcastClusterName(String env) {
        return "hazelcast-${env}"
    }

}
