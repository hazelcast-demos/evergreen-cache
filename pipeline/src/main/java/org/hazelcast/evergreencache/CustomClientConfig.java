package org.hazelcast.evergreencache;

import com.hazelcast.client.config.ClientConfig;

public class CustomClientConfig extends ClientConfig {

    public CustomClientConfig(String cacheHost) {
        getNetworkConfig().addAddress(cacheHost != null ? cacheHost : "localhost");
    }
}