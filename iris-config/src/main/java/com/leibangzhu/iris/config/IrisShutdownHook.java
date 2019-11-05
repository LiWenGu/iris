package com.leibangzhu.iris.config;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
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
        log.info("开启优雅关机.");
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
        log.info("关闭协议层 client、server 的连接");
        destroyProtocols();
        log.info("删除注册中心的相关信息");
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
        ExtensionLoader<RegistryFactory> loader = ExtensionLoader.getExtensionLoader(RegistryFactory.class);
        for (String registryName : loader.getSupportedExtensions()) {
            try {
                RegistryFactory registryFactory = loader.getExtension(registryName);
                if (registryFactory != null) {
                    for (Registry registry : registryFactory.getAllRegistry()) {
                        registry.destroy();
                    }
                }
            } catch (Throwable t) {
                log.warn(t.getMessage(), t);
            }
        }
    }
}
