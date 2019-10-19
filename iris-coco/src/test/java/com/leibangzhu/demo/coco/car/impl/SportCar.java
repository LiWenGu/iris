/*
 * Copyright 2012-2013 coco Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leibangzhu.demo.coco.car.impl;

import com.leibangzhu.demo.coco.car.Car;
import com.leibangzhu.demo.coco.wheel.Wheel;

import java.util.Map;

/**
 * @author Jerry Lee(oldratlee<at>gmail<dot>com)
 */
public class SportCar implements Car {

    private Wheel wheel;

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

    public void run(Map<String, String> config) {
        wheel.roll(config);
        System.out.println("SportCar Running...");
    }
}
