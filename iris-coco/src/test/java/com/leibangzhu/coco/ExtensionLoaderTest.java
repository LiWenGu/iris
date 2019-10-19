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

import com.leibangzhu.coco.ext1.SimpleExt;
import com.leibangzhu.coco.ext1.impl.SimpleExtImpl1;
import com.leibangzhu.coco.ext1.impl.SimpleExtImpl2;
import com.leibangzhu.coco.ext2.ConfigHolder;
import com.leibangzhu.coco.ext2.NoDefaultExt;
import com.leibangzhu.coco.ext3.WrappedExt;
import com.leibangzhu.coco.ext3.impl.Ext3Impl1;
import com.leibangzhu.coco.ext3.impl.Ext3Impl2;
import com.leibangzhu.coco.ext3.impl.Ext3Wrapper1;
import com.leibangzhu.coco.ext3.impl.Ext3Wrapper2;
import com.leibangzhu.coco.ext4.WithAttributeExt;
import com.leibangzhu.coco.ext5.NoAdaptiveMethodExt;
import com.leibangzhu.coco.ext6.InjectExt;
import com.leibangzhu.coco.ext6.impl.Ext6Impl2;
import com.leibangzhu.coco.ext7.InitErrorExt;
import com.leibangzhu.coco.ext8.InvalidNameExt;
import com.leibangzhu.coco.ext8.InvalidNameExt2;
import com.leibangzhu.coco.ext9.ManualAdaptiveClassExt;
import com.leibangzhu.coco.ext9.impl.ManualAdaptive;
import com.leibangzhu.coco.exta.ImplNoDefaultConstructorExt;
import com.leibangzhu.util.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * @author Jerry Lee(oldratlee AT gmail DOT com)
 */
public class ExtensionLoaderTest {
    @Test
    public void test_getExtensionLoader_Null() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    containsString("Extension type == null"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotInterface() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(ExtensionLoaderTest.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    containsString("type(com.leibangzhu.coco.ExtensionLoaderTest) is not interface"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotSpiAnnotation() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(NoExtensionExt.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    allOf(containsString(NoExtensionExt.class.getName()),
                            containsString("is not a extension"),
                            containsString("WITHOUT @Extension Annotation")));
        }
    }

    @Test
    public void test_getDefault() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtension();
        assertThat(ext, instanceOf(SimpleExtImpl1.class));

        String name = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        assertEquals("impl1", name);
    }

    @Test
    public void test_getDefault_NULL() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getDefaultExtension();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No default extension on extension com.leibangzhu.coco.ext2.NoDefaultExt"));
        }


        String name = ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getDefaultExtensionName();
        assertNull(name);
    }

    @Test
    public void test_getExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1") instanceof SimpleExtImpl1);
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl2") instanceof SimpleExtImpl2);
    }

    @Test
    public void test_getExtension_sameInstance() throws Exception {
        ExtensionLoader<SimpleExt> extensionLoader = ExtensionLoader.getExtensionLoader(SimpleExt.class);
        assertSame(extensionLoader.getExtension("impl1"), extensionLoader.getExtension("impl1"));
    }

    @Test
    public void test_getExtension_WithWrapper_notAutoLoad() throws Exception {
        //
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1", new ArrayList<String>());
        assertThat(impl1, instanceOf(Ext3Impl1.class));

        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2", new ArrayList<String>());
        assertThat(impl2, instanceOf(Ext3Impl2.class));

        int echoCount1 = Ext3Wrapper1.echoCount.get();
        int echoCount2 = Ext3Wrapper2.echoCount.get();
        int yellCount1 = Ext3Wrapper1.yellCount.get();
        int yellCount2 = Ext3Wrapper2.yellCount.get();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        assertEquals("Ext3Impl1-echo", impl1.echo(config, "ha"));
        assertEquals(echoCount1, Ext3Wrapper1.echoCount.get());
        assertEquals(echoCount2, Ext3Wrapper2.echoCount.get());
        assertEquals(yellCount1, Ext3Wrapper1.yellCount.get());
        assertEquals(yellCount2, Ext3Wrapper2.yellCount.get());

        assertEquals("Ext3Impl2-yell", impl2.yell(config, "ha"));
        assertEquals(echoCount1, Ext3Wrapper1.echoCount.get());
        assertEquals(echoCount2, Ext3Wrapper2.echoCount.get());
        assertEquals(yellCount1, Ext3Wrapper1.yellCount.get());
        assertEquals(yellCount2, Ext3Wrapper2.yellCount.get());
    }

    @Test
    public void test_getExtension_WithWrapper() throws Exception {
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1", Arrays.asList("wrapper1", "wrapper2"));
        assertThat(impl1, anyOf(instanceOf(Ext3Wrapper1.class), instanceOf(Ext3Wrapper2.class)));

        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2", Arrays.asList("wrapper1", "wrapper2"));
        assertThat(impl2, anyOf(instanceOf(Ext3Wrapper1.class), instanceOf(Ext3Wrapper2.class)));

        int echoCount1 = Ext3Wrapper1.echoCount.get();
        int echoCount2 = Ext3Wrapper2.echoCount.get();
        int yellCount1 = Ext3Wrapper1.yellCount.get();
        int yellCount2 = Ext3Wrapper2.yellCount.get();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        assertEquals("Ext3Impl1-echo", impl1.echo(config, "ha"));
        assertEquals(echoCount1 + 1, Ext3Wrapper1.echoCount.get());
        assertEquals(echoCount2 + 1, Ext3Wrapper2.echoCount.get());
        assertEquals(yellCount1, Ext3Wrapper1.yellCount.get());
        assertEquals(yellCount2, Ext3Wrapper2.yellCount.get());

        assertEquals("Ext3Impl2-yell", impl2.yell(config, "ha"));
        assertEquals(echoCount1 + 1, Ext3Wrapper1.echoCount.get());
        assertEquals(echoCount2 + 1, Ext3Wrapper2.echoCount.get());
        assertEquals(yellCount1 + 1, Ext3Wrapper1.yellCount.get());
        assertEquals(yellCount2 + 1, Ext3Wrapper2.yellCount.get());
    }

    @Test
    public void test_getExtension_ExceptionNoExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("NotExisted");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.leibangzhu.coco.ext1.SimpleExt by name NotExisted"));
        }
    }

    @Test
    public void test_getExtension_ExceptionNoExtension_NameOnWrapperNoEffective() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("wrapper1");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.leibangzhu.coco.ext3.WrappedExt by name wrapper1"));
        }
    }

    @Test
    public void test_getExtension_ExceptionNullArg() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    public void test_hasExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1"));
        assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1,impl2"));
        assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("xxx"));

        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    public void test_hasDefaultExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasDefaultExtension());
        assertFalse(ExtensionLoader.getExtensionLoader(NoDefaultExt.class).hasDefaultExtension());
    }

    @Test
    public void test_hasExtension_wrapperIsNotExt() throws Exception {
        ExtensionLoader<WrappedExt> extensionLoader = ExtensionLoader.getExtensionLoader(WrappedExt.class);

        assertTrue(extensionLoader.hasExtension("impl1"));
        assertFalse(extensionLoader.hasExtension("impl1,impl2"));
        assertFalse(extensionLoader.hasExtension("xxx"));

        assertFalse(extensionLoader.hasExtension("wrapper1"));

        try {
            extensionLoader.hasExtension(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Extension name == null"));
        }
    }

    @Test
    public void test_getSupportedExtensions() throws Exception {
        Set<String> extensions = ExtensionLoader.getExtensionLoader(SimpleExt.class).getSupportedExtensions();

        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");
        expected.add("impl3");

        assertEquals(expected, extensions);
    }

    @Test
    public void test_getSupportedExtensions_wrapperIsNotExt() throws Exception {
        Set<String> extensions = ExtensionLoader.getExtensionLoader(WrappedExt.class).getSupportedExtensions();

        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");

        assertEquals(expected, extensions);
    }

    @Test
    public void test_getExtensionAttribute_all() throws Exception {
        ExtensionLoader<WithAttributeExt> extensionLoader = ExtensionLoader.getExtensionLoader(WithAttributeExt.class);

        Map<String, Map<String, String>> extensionAttribute = extensionLoader.getExtensionAttribute();

        Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("impl1", Utils.kv2Map("k1", "v1", "k2", "", "k3", "v3", "k4", "", "k5", "v5"));
        expected.put("impl2", new HashMap<String, String>());
        expected.put("impl3", Utils.kv2Map("key1", "value1", "key2", "value2", "key3", ""));

        assertEquals(expected, extensionAttribute);
    }

    @Test
    public void test_getExtensionAttribute_one() throws Exception {
        ExtensionLoader<WithAttributeExt> extensionLoader = ExtensionLoader.getExtensionLoader(WithAttributeExt.class);
        Map<String, String> extensionAttribute = extensionLoader.getExtensionAttribute("impl1");
        assertEquals(Utils.kv2Map("k1", "v1", "k2", "", "k3", "v3", "k4", "", "k5", "v5"), extensionAttribute);
    }

    @Test
    public void test_getExtensionAttribute_oneNotExisted() throws Exception {
        ExtensionLoader<WithAttributeExt> extensionLoader = ExtensionLoader.getExtensionLoader(WithAttributeExt.class);

        try {
            Map<String, String> extensionAttribute = extensionLoader.getExtensionAttribute("NotExisted");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension com.leibangzhu.coco.ext4.WithAttributeExt by name NotExisted"));
        }
    }


    @Test
    public void test_getAdaptiveInstance_defaultExtension() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        String echo = ext.echo(config, "haha");
        assertEquals("Ext1Impl1-echo", echo);
    }

    @Test
    public void test_getAdaptiveInstance_useTypeNameAsKey() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1", "simple.ext", "impl2");

        String echo = ext.echo(config, "haha");
        assertEquals("Ext1Impl2-echo", echo);
    }

    @Test
    public void test_getAdaptiveInstance_customizeKey() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1", "key2", "impl2");

        String echo = ext.yell(config, "haha");
        assertEquals("Ext1Impl2-yell", echo);

        config.put("key1", "impl3");
        echo = ext.yell(config, "haha");
        assertEquals("Ext1Impl3-yell", echo);
    }

    @Test
    public void test_getAdaptiveInstance_ConfigNpe() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();

        try {
            ext.echo(null, "haha");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("adaptive java.util.Map argument == null", e.getMessage());
        }
    }

    @Test
    public void test_getAdaptiveInstance_ExceptionWhenNoAdaptiveMethodOnInterface() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(NoAdaptiveMethodExt.class).getAdaptiveInstance();
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(),
                    allOf(containsString("Fail to create adaptive extension for extension point com.leibangzhu.coco.ext5.NoAdaptiveMethodExt"),
                            containsString("No adaptive method on extension com.leibangzhu.coco.ext5.NoAdaptiveMethodExt, refuse to create the adaptive class")));
        }
        // 多次get，都会报错且相同
        try {
            ExtensionLoader.getExtensionLoader(NoAdaptiveMethodExt.class).getAdaptiveInstance();
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(),
                    allOf(containsString("Fail to create adaptive extension for extension point com.leibangzhu.coco.ext5.NoAdaptiveMethodExt"),
                            containsString("No adaptive method on extension com.leibangzhu.coco.ext5.NoAdaptiveMethodExt, refuse to create the adaptive class")));
        }
    }

    @Test
    public void test_getAdaptiveInstance_ExceptionWhenNotAdaptiveMethod() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();


        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");
        try {
            ext.bang(config, 33);
            fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected.getMessage(), containsString("method "));
            assertThat(
                    expected.getMessage(),
                    containsString("of interface com.leibangzhu.coco.ext1.SimpleExt is not adaptive method!"));
        }
    }

    @Test
    public void test_getAdaptiveInstance_ManualAdaptiveClassExt() throws Exception {
        ExtensionLoader<ManualAdaptiveClassExt> extensionLoader = ExtensionLoader.getExtensionLoader(ManualAdaptiveClassExt.class);
        Map<String, String> config = Utils.kv2Map("key", "impl2");

        ManualAdaptiveClassExt impl1 = extensionLoader.getExtension("impl1");
        assertEquals("Ext9Impl1-echo", impl1.echo(config, ""));

        ManualAdaptiveClassExt adaptiveInstance = extensionLoader.getAdaptiveInstance();
        assertEquals("Ext9Impl2-echo" + ManualAdaptive.class.getName(), adaptiveInstance.echo(config, ""));
    }

    @Test
    public void test_configHolder_getAdaptiveInstance() throws Exception {
        NoDefaultExt ext = ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1", "no.default.ext", "impl1");

        ConfigHolder holder = new ConfigHolder();
        holder.setConfig(config);

        String echo = ext.echo(holder, "haha");
        assertEquals("Ext2Impl1-echo", echo);
    }

    @Test
    public void test_configHolder_getAdaptiveInstance_noExtension() throws Exception {
        NoDefaultExt ext = ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        ConfigHolder holder = new ConfigHolder();
        holder.setConfig(config);

        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Fail to get extension("));
        }

        config.put("no.default.ext", "XXX");
        holder.setConfig(config);
        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("No such extension"));
        }
    }

    @Test
    public void test_configHolder_getAdaptiveInstance_ConfigNpe() throws Exception {
        NoDefaultExt ext = ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getAdaptiveInstance();

        try {
            ext.echo(null, "haha");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("adaptive com.leibangzhu.coco.ext2.ConfigHolder argument == null", e.getMessage());
        }

        try {
            ext.echo(new ConfigHolder(), "haha");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("adaptive com.leibangzhu.coco.ext2.ConfigHolder argument getConfig() == null", e.getMessage());
        }
    }

    @Test
    public void test_configHolder_getAdaptiveInstance_ExceptionWhenNotAdaptiveMethod() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        try {
            ext.bang(config, 33);
            fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected.getMessage(), containsString("method "));
            assertThat(
                    expected.getMessage(),
                    containsString("of interface com.leibangzhu.coco.ext1.SimpleExt is not adaptive method!"));
        }
    }

    @Test
    public void test_configHolder_getAdaptiveInstance_ExceptionWhenNameNotProvided() throws Exception {
        NoDefaultExt ext = ExtensionLoader.getExtensionLoader(NoDefaultExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1");

        ConfigHolder holder = new ConfigHolder();
        holder.setConfig(config);

        try {
            ext.echo(holder, "impl1");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Fail to get extension("));
        }

        config.put("key1", "impl1");
        holder.setConfig(config);
        try {
            ext.echo(holder, "haha");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Fail to get extension(com.leibangzhu.coco.ext2.NoDefaultExt) name from argument"));
        }
    }

    @Test
    public void test_getAdaptiveInstance_inject() throws Exception {
        InjectExt ext = ExtensionLoader.getExtensionLoader(InjectExt.class).getAdaptiveInstance();

        Map<String, String> config = Utils.kv2Map("protocol", "p1", "host", "1.2.3.4", "port", "1010", "path", "path1", "key", "impl1");

        assertEquals("Ext6Impl1-echo-Ext1Impl1-echo", ext.echo(config, "ha"));

        config.put("simple.ext", "impl2");
        assertEquals("Ext6Impl1-echo-Ext1Impl2-echo", ext.echo(config, "ha"));
    }

    @Test
    public void test_getAdaptiveInstance_InjectNotExtFail() throws Exception {
        InjectExt ext = ExtensionLoader.getExtensionLoader(InjectExt.class).getExtension("impl2");

        Ext6Impl2 impl = (Ext6Impl2) ext;
        assertNull(impl.getList());
    }

    @Test
    public void test_InitError() throws Exception {
        ExtensionLoader<InitErrorExt> loader = ExtensionLoader.getExtensionLoader(InitErrorExt.class);

        loader.getExtension("ok");

        try {
            loader.getExtension("error");
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("Failed to load config line(error=com.leibangzhu.coco.ext7.impl.Ext7InitErrorImpl)"));
            assertThat(expected.getMessage(), containsString("com.leibangzhu.coco.ext7.InitErrorExt) for extension(com.leibangzhu.coco.ext7.InitErrorExt)"));
            assertThat(expected.getCause().getCause(), instanceOf(ExceptionInInitializerError.class));
        }
    }

    @Test
    public void test_InvalidExtName() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(InvalidNameExt.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(),
                    containsString("default name(invalid-name&) of extension com.leibangzhu.coco.ext8.InvalidNameExt is invalid"));
        }
    }

    // 新增测试 a-Z 中的 ] [ 等非法符号，见 Issue #56
    @Test
    public void test_InvalidExtName2() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(InvalidNameExt2.class);
            fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(),
                    containsString("default name(invalidName]) of extension com.leibangzhu.coco.ext8.InvalidNameExt2 is invalid"));
        }
    }

    @Test
    public void test_ImplNoDefaultConstructor() throws Exception {
        ExtensionLoader<ImplNoDefaultConstructorExt> extensionLoader = ExtensionLoader.getExtensionLoader(ImplNoDefaultConstructorExt.class);
        assertEquals(new HashSet<String>(Arrays.asList("impl2")), extensionLoader.getSupportedExtensions());
    }
}
