package com.leibangzhu.iris.registry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public enum RegistryTypeEnum {

    consumers("consumers"),
    configurators("configurators"),
    routers("routers"),
    providers("providers"),
    ;

    private String name;

    RegistryTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
