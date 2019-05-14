package com.aimlesshammer.pocpapispringboot;

import java.util.HashMap;
import java.util.Map;

public class SpringServerProperties {
    private final HashMap<String, Object> props = new HashMap<>();

    public SpringServerProperties withPort(int portNumber) {
        props.put("server.port", portNumber);
        return this;
    }

    public Map<String, Object> asMap() {
        return props;
    }
}
