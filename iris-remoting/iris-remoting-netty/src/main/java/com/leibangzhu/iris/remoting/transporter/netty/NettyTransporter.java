/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.core.HelloService;
import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.etcd.EtcdRegistry;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.Server;
import com.leibangzhu.iris.remoting.Transporter;

import java.util.ArrayList;
import java.util.List;

public class NettyTransporter implements Transporter {

    @Override
    public Server bind(IRegistry registry, int port) throws Exception {
        registry = new EtcdRegistry("http://127.0.0.1:2379");
        Server server = new NettyServer();
        server.init(registry, 2017);
        server.export(IHelloService.class, new HelloService());
        server.run();
        return server;
    }

    @Override
    public Client connect(IRegistry registry) throws Exception {
        registry = new EtcdRegistry("http://127.0.0.1:2379");
        Client client = new NettyClient();
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(IHelloService.class.getName());
        client.init(registry, serviceNames);
        return client;
    }
}
