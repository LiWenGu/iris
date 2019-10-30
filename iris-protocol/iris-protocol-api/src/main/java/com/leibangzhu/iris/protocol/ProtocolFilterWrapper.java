package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;

import java.util.Set;

public class ProtocolFilterWrapper implements Protocol {

    private final Protocol protocol;

    public ProtocolFilterWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == null");
        }
        this.protocol = protocol;
    }

    private static void buildFilterChain(Class<?> clazz, Object handler) throws Exception {
        ExtensionLoader<Filter> extensionLoader = ExtensionLoader.getExtensionLoader(Filter.class);
        Set<String> filtersNames = extensionLoader.getSupportedExtensions();
        for (String filtersName : filtersNames) {
            Filter filter = extensionLoader.getExtension(filtersName);
            filter.invoke(clazz, handler);
        }
    }

    @Override
    public void export(Class<?> clazz, Object handler) throws Exception {
        buildFilterChain(clazz, handler);
        protocol.export(clazz, handler);
    }

    @Override
    public <T> T ref(Class<T> clazz) throws Exception {
        buildFilterChain(clazz, null);
        return protocol.ref(clazz);
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }
}
