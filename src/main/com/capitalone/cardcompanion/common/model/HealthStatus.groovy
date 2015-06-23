//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.model

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Health status.
 */
class HealthStatus {
    @JsonProperty("db_healthy")
    Boolean             dbHealthy
    String              name
    @JsonProperty("service_statuses")
    Map<String, String> serviceStatuses
    String              version

    @Override
    String toString() {
        ReflectiveRepresentation.toString(this)
    }
}
