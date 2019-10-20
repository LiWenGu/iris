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
import com.leibangzhu.iris.registry.IRegistry;
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
public class EtcdRegistry implements IRegistry {

    private final String rootPath = "iris";
    private Lease lease;
    private KV kv;
    private Watch watch;
    private long leaseId;

    private Map<String, List<Endpoint>> endpointsByService = new LinkedHashMap<>();
    private IEventCallback callback;

    public EtcdRegistry(String registryAddress) throws Exception {
        Client client = Client.builder().endpoints(registryAddress).build();
        this.lease = client.getLeaseClient();
        this.kv = client.getKVClient();
        this.watch = client.getWatchClient();
        this.leaseId = lease.grant(300).get().getID();
    }

    // 向ETCD中注册服务
    public void register(String serviceName, int port) throws Exception {
        // 服务注册的key为:    /iris/com.some.package.IHelloService/consumers/
        String strKey = MessageFormat.format("/{0}/{1}/{2}", rootPath, serviceName);
        String strVal = MessageFormat.format("{0}:{1}", IpHelper.getHostIp(), String.valueOf(port));
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString(strVal);
        kv.put(key, val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        System.out.println("Register a new service at:" + strKey);
    }

    // 向ETCD中注册服务
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

    // 取消注册服务
    public void unRegistered(String serviceName, int port, RegistryTypeEnum type) throws Exception {
// 服务注册的key为:    /iris/com.some.package.IHelloService/consumers/127.0.0.1:20070
        String strKey = MessageFormat.format("/{0}/{1}/{2}/{3}:{4}", rootPath, serviceName, type.getName(), IpHelper.getHostIp(), String.valueOf(port));
        String strVal = "";
        ByteSequence key = ByteSequence.fromString(strKey);
        kv.delete(key).get();
        System.out.println("unregister a new service at:" + strKey);
    }

    // 取消注册服务
    public void unRegistered(String serviceName) {
    }

    public List<Endpoint> find(String serviceName) throws Exception {

        if (endpointsByService.containsKey(serviceName)) {
            return endpointsByService.get(serviceName);
        }

        String strKey = MessageFormat.format("/{0}/{1}", rootPath, serviceName);   //    /iris/com.leibangzhu.IHelloService
        ByteSequence key = ByteSequence.fromString(strKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        List<Endpoint> endpoints = new ArrayList<>();

        for (KeyValue kv : response.getKvs()) {
            String s = kv.getKey().toStringUtf8();
            int index = s.lastIndexOf("/");
            String endpointStr = s.substring(index + 1, s.length());

            String host = endpointStr.split(":")[0];
            int port = Integer.valueOf(endpointStr.split(":")[1]);

            //System.out.println(host);
            //System.out.println(port);

            endpoints.add(new Endpoint(host, port));
        }
        endpointsByService.put(serviceName, endpoints);
        return endpoints;
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

    @Override
    public void watch(IEventCallback callback) {
        this.callback = callback;
        watch();
    }

    private void watch() {
        Watch.Watcher watcher = watch.watch(ByteSequence.fromString("/" + rootPath),
                WatchOption.newBuilder().withPrefix(ByteSequence.fromString("/" + rootPath)).build());

        Executors.newSingleThreadExecutor().submit((Runnable) () -> {
            while (true) {
                try {
                    for (WatchEvent event : watcher.listen().getEvents()) {
                        System.out.println(event.getEventType());
                        System.out.println(event.getKeyValue().getKey().toStringUtf8());
                        System.out.println(event.getKeyValue().getValue().toStringUtf8());

                        // /iris/com.leibangzhu.IHelloService/192.168.41.215:2000

                        String s = event.getKeyValue().getKey().toStringUtf8();
                        String serviceName = s.split("/")[2];
                        String endpoint = s.split("/")[3];

                        String host = endpoint.split(":")[0];
                        int port = Integer.valueOf(endpoint.split(":")[1]);

                        endpointsByService.get(serviceName).remove(new Endpoint(host, port));

                        if (null != callback) {
                            RegistryEvent registryEvent = RegistryEvent
                                    .newBuilder()
                                    .eventType(RegistryEvent.EventType.valueOf(event.getEventType().toString()))
                                    .key(event.getKeyValue().getKey().toStringUtf8())
                                    .value(event.getKeyValue().getValue().toStringUtf8())
                                    .build();

                            callback.execute(registryEvent);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
