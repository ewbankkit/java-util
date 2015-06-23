//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.jaxrs;

import javax.ws.rs.core.MediaType;

/**
 * Static utility methods pertaining to media types.
 */
public final class MediaTypes {
    private MediaTypes() {}

    public static final String    APPLICATION_VND_APPLE_PKPASS      = "application/vnd.apple.pkpass";
    public static final MediaType APPLICATION_VND_APPLE_PKPASS_TYPE = new MediaType("application", "vnd.apple.pkpass");
}
