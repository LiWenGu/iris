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
package com.leibangzhu.iris.container;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.IrisConfig;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main. (API, Static, ThreadSafe)
 * <p>
 * This class is entry point loading containers.
 */
@Slf4j
public class Main {

    public static final String CONTAINER_KEY = "iris.container";

    public static final String SHUTDOWN_HOOK_KEY = "iris.shutdown.hook";

    private static final ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final Condition STOP = LOCK.newCondition();

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                String config = IrisConfig.get(CONTAINER_KEY, loader.getDefaultExtensionName());
                args = config.split(",");
            }

            final List<Container> containers = new ArrayList<Container>();
            for (int i = 0; i < args.length; i++) {
                containers.add(loader.getExtension(args[i]));
            }
            log.info("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
                Runtime.getRuntime().addShutdownHook(new Thread("iris-container-shutdown-hook") {
                    @Override
                    public void run() {
                        for (Container container : containers) {
                            try {
                                container.stop();
                                log.info("Iris " + container.getClass().getSimpleName() + " stopped!");
                            } catch (Throwable t) {
                                log.error(t.getMessage(), t);
                            }
                            try {
                                LOCK.lock();
                                STOP.signal();
                            } finally {
                                LOCK.unlock();
                            }
                        }
                    }
                });
            }

            for (Container container : containers) {
                container.start();
                log.info("Iris " + container.getClass().getSimpleName() + " started!");
            }
            System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " Dubbo service server started!");
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
        try {
            LOCK.lock();
            STOP.await();
        } catch (InterruptedException e) {
            log.warn("Iris service server stopped, interrupted by other thread!", e);
        } finally {
            LOCK.unlock();
        }
    }

}