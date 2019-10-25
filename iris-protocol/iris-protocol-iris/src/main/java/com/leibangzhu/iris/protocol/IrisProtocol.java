package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.IrisConfig;
import com.leibangzhu.iris.core.NameThreadFactory;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.Server;
import com.leibangzhu.iris.remoting.Transporter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IrisProtocol implements Protocol {
    /**
     * 消息的开头的信息标志
     */
    public static int head_data = 0x76;
    public static int req = 1;
    public static int res = 0;
    /**
     * 消息的长度
     */
    private int contentLength;
    /**
     * 消息的内容
     */
    private byte[] content;

    public int getHead_data() {
        return head_data;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SmartCarProtocol [head_data=" + head_data + ", contentLength="
                + contentLength + ", content=" + Arrays.toString(content) + "]";
    }

    @Override
    public void destroy() {
        server.destory();
        client.destroy();
    }

    private Server server;

    private Client client;

    private static final ScheduledExecutorService DELAY_EXPORT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("IrisServiceDelayExporter"));

    @Override
    public void export(Class<?> clazz, Object handler) throws Exception {
        if (server == null) {
            synchronized (this) {
                if (server == null) {
                    Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
                    RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getDefaultExtension();
                    Registry registry = registryFactory.getRegistry("http://127.0.0.1:2379");
                    server = transporter.bind(registry, 0);
                    server.init(registry, 2017);
                    server.run();
                }
            }
        }
        // todo 延迟暴露。需要根据 class 来针对处理吗？
        int delay = IrisConfig.get("iris.delay", -1);
        if (delay != -1) {
            DELAY_EXPORT_EXECUTOR.schedule(() -> {
                try {
                    server.export(clazz, handler);
                } catch (Exception e) {
                    log.error("延迟暴露失败" + e.getMessage());
                }
            }, delay, TimeUnit.MILLISECONDS);
        } else {
            server.export(clazz, handler);
        }
    }

    @Override
    public <T> T ref(Class<T> clazz) throws Exception {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
                    RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getDefaultExtension();
                    Registry registry = registryFactory.getRegistry("http://127.0.0.1:2379");
                    client = transporter.connect(registry);
                    List<String> serviceNames = new ArrayList<>();
                    serviceNames.add(clazz.getName());
                    client.init(registry, serviceNames);
                }
            }
        }
        T t = client.ref(clazz);
        return t;
    }


}
