package com.leibangzhu.iris.remoting;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class URL implements Serializable {

    private static final long serialVersionUID = -1985165475234910535L;

    private String protocol;

    private String username;

    private String password;

    // by default, host to registry
    private String host;

    // by default, port to registry
    private int port;

    private String path;

    private Map<String, String> parameters;

    // ==== cache ====

    private volatile transient Map<String, String> params;

    private volatile transient Map<String, URL> urls;

    private volatile transient String ip;

    private volatile transient String full;

    private volatile transient String identity;

    private volatile transient String parameter;

    private volatile transient String string;
}
