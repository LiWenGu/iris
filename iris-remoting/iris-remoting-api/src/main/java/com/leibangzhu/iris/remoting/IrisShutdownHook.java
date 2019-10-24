package com.leibangzhu.iris.remoting;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.registry.Registry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class IrisShutdownHook extends Thread {

    private static final IrisShutdownHook IRIS_SHUTDOWN_HOOK = new IrisShutdownHook("IrisShutdownHook");

    private final AtomicBoolean registered = new AtomicBoolean(false);

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private IrisShutdownHook(String name) {
        super(name);
    }

    public static IrisShutdownHook getIrisShutdownHook() {
        return IRIS_SHUTDOWN_HOOK;
    }

    @Override
    public void run() {
        log.info("Run shutdown hook now.");
        doDestroy();
    }

    public void register() {
        if (!registered.get() && registered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(getIrisShutdownHook());
        }
    }

    public void unregister() {
        if (registered.get() && registered.compareAndSet(true, false)) {
            Runtime.getRuntime().removeShutdownHook(getIrisShutdownHook());
        }
    }

    private void doDestroy() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        //destroyProtocols();
        destroyRegistry();
    }

    private void destroyProtocols() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);

        for (String protocolName : loader.getSupportedExtensions()) {
            try {
                Protocol protocol = loader.getExtension(protocolName);
                if (protocol != null) {
                    protocol.destroy();
                }
            } catch (Throwable t) {
                log.warn(t.getMessage(), t);
            }
        }
    }

    private void destroyRegistry() {
        ExtensionLoader<Registry> loader = ExtensionLoader.getExtensionLoader(Registry.class);
        for (String registryName : loader.getSupportedExtensions()) {
            try {
                Registry registry = loader.getExtension(registryName);
                if (registry != null) {
                    registry.destroy();
                }
            } catch (Throwable t) {
                log.warn(t.getMessage(), t);
            }
        }
    }
}
