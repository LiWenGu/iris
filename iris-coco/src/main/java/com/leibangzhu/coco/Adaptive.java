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

package com.leibangzhu.coco;

import com.leibangzhu.coco.support.MapSourceExtractor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解到扩展点方法的参数，表示这个参数用于提供信息，让自适应实例（Adaptive Instance）找到运行调用时要调用的扩展名称。
 *
 * @author Jerry Lee(oldratlee AT gmail DOT com)
 * @see ExtensionLoader
 * @see Extension
 * @see NameExtractor
 * @since 0.1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Adaptive {
    /**
     * Key列表，这些Key在所注解参数的对应的Value是自适应实例方法调用时真正执行调用的扩展的名称。
     * <br/>
     * 如果这些Key在中所注解参数都没有Value，自适应实例会使用缺省扩展（在扩展点接口的{@link Extension}上设置的）。
     * <p/>
     * eg. <code>String[] {"key1", "key2"}</code>表示：
     * <ol>
     * <li>使用<code>key1</code>的值作为自适应实例真正调用的扩展实现的名称。
     * <li>如果<code>key1</code>没有对应的值，则使用<code>key2</code>的值作为自适应实例真正调用的扩展实现的名称。
     * <li>如果<code>key2</code>没有对应的值，则使用缺省扩展。
     * <li>如果没有缺省扩展，则在获取自适应实例时，会抛出{@link IllegalStateException}。
     * </ol>
     * <p/>
     * 缺省是扩展点接口名的点分小写形式。
     * eg. 扩展点接口名<code>com.leibangzhu.coco.FooBizService</code>, 缺省key是<code>String[] {"foo.biz.service"}</code>。
     *
     * @see Extension#defaultValue()
     * @since 0.1.0
     */
    String[] value() default {};

    /**
     * Adaptive Instance执行扩展点调用时，通过{@link NameExtractor#extract(Object)}方法
     * 从扩展点方法参数上提取实际要调用的扩展名称。
     *
     * @since 0.3.0
     */
    Class<? extends NameExtractor> extractor() default MapSourceExtractor.class;
}
