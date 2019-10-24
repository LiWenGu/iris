package com.leibangzhu.iris.registry.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.leibangzhu.iris.core.Endpoint;
import com.leibangzhu.iris.core.IpHelper;
import com.leibangzhu.iris.core.NameThreadFactory;
import com.leibangzhu.iris.registry.IEventCallback;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryEvent;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Slf4j
public class EtcdRegistry implements Registry {

    private final String rootPath = "iris";
    Client client;
    private Lease lease;
    private KV kv;
    private Watch watch;
    private long leaseId;

    // 用于消费者找提供者们
    private Map<String, List<Endpoint>> endpointsByService = new LinkedHashMap<>();
    // 用于消费者和提供者保存在注册中心的值
    private Map<String, List<Endpoint>> endpointsByService = new LinkedHashMap<>();

    public EtcdRegistry(String registryAddress) throws Exception {
        client = Client.builder().endpoints(registryAddress).build();
        this.lease = client.getLeaseClient();
        this.kv = client.getKVClient();
        this.watch = client.getWatchClient();
        this.leaseId = lease.grant(30).get().getID();
    }

    // 向ETCD中注册服务
    @Override
    public void register(String serviceName, int port, RegistryTypeEnum type) throws Exception {
        // 服务注册的key为:    /iris/com.some.package.IHelloService/consumers/127.0.0.1:20070
        String strKey = MessageFormat.format("/{0}/{1}/{2}/{3}:{4}", rootPath, serviceName, type.getName(), IpHelper.getHostIp(), String.valueOf(port));
        String strVal = "";
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString(strVal);
        kv.put(key, val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        System.out.println("Register a new service at:" + strKey);
    }

    // 发送心跳到ETCD,表明该host是活着的
    @Override
    public void keepAlive() {
        Executors.newSingleThreadExecutor(new NameThreadFactory("etcd-keepAlive")).submit(
                () -> {
                    try {
                        Lease.KeepAliveListener listener = lease.keepAlive(leaseId);
                        listener.listen();
                        log.info("KeepAlive lease:" + leaseId + "; Hex format:" + Long.toHexString(leaseId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void destroy() {
        if (watch != null) {
            watch.close();
        }
        if (client != null) {
            client.close();
        }
    }

    // 取消注册服务
    public void unRegistered(String serviceName, int port, RegistryTypeEnum type) throws Exception {
// 服务注册的key为:    /iris/com.some.package.IHelloService/consumers/127.0.0.1:20070
        String strKey = MessageFormat.format("/{0}/{1}/{2}/{3}:{4}", rootPath, serviceName, type.getName(), IpHelper.getHostIp(), String.valueOf(port));
        String strVal = "";
        ByteSequence key = ByteSequence.fromString(strKey);
        kv.delete(key).get();
        System.out.println("unregister a new service at:" + strKey);
    }

    @Override
    public void subscribe(String serviceName, RegistryTypeEnum registryTypeEnum, IEventCallback iEventCallback) {
        Watch.Watcher watcher = watch.watch(ByteSequence.fromString("/" + rootPath + "/" + serviceName + "/" + registryTypeEnum.getName()),
                WatchOption.newBuilder().withPrefix(ByteSequence.fromString("/" + rootPath + serviceName + "/" + registryTypeEnum.getName())).build());

        Executors.newSingleThreadExecutor().submit((Runnable) () -> {
            while (true) {
                try {
                    for (WatchEvent event : watcher.listen().getEvents()) {
                        String s = event.getKeyValue().getKey().toStringUtf8();
                        if (null != iEventCallback) {
                            RegistryEvent registryEvent = RegistryEvent
                                    .newBuilder()
                                    .eventType(RegistryEvent.EventType.valueOf(event.getEventType().toString()))
                                    .key(event.getKeyValue().getKey().toStringUtf8())
                                    .value(event.getKeyValue().getValue().toStringUtf8())
                                    .build();

                            iEventCallback.execute(registryEvent);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<Endpoint> find(String serviceName, RegistryTypeEnum typeEnum) throws Exception {
        String bizKey = serviceName + "/" + typeEnum.getName();
        if (endpointsByService.containsKey(bizKey)) {
            return endpointsByService.get(bizKey);
        }

        String strKey = MessageFormat.format("/{0}/{1}", rootPath, bizKey);   //    /iris/com.leibangzhu.IHelloService/consumers
        ByteSequence key = ByteSequence.fromString(strKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        List<Endpoint> endpoints = new ArrayList<>();

        for (KeyValue kv : response.getKvs()) {
            String s = kv.getKey().toStringUtf8();
            int index = s.lastIndexOf("/");
            String endpointStr = s.substring(index + 1);

            String host = endpointStr.split(":")[0];
            int port = Integer.parseInt(endpointStr.split(":")[1]);

            //System.out.println(host);
            //System.out.println(port);

            endpoints.add(new Endpoint(host, port));
        }
        endpointsByService.put(serviceName, endpoints);
        return endpoints;
    }
}
