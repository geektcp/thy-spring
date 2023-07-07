package com.geektcp.common.spring.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by tanghaiyang on 2018/8/17.
 */
public enum StorageType {
    TDB("TDB", false, new String[]{}),
    UNKNOWN("unknown", false, new String[]{});

    private static final Map<String, StorageType> codeLookup = new ConcurrentHashMap<>(6);

    static {
        for (StorageType type : EnumSet.allOf(StorageType.class)){
            codeLookup.put(type.name.toLowerCase(), type);
        }
    }

    private boolean isPlatform;
    private String name;
    private String[] configFile;

    StorageType(String name, boolean isPlatform, String[] configFile){
        this.name = name;
        this.isPlatform = isPlatform;
        this.configFile = configFile;
    }

    public String getName() {
        return name;
    }

    public boolean isHadoopPlatform() {
        return isPlatform;
    }

    public String[] getConfigFile() {
        return configFile;
    }

    public static StorageType fromCode(String code) {
        if (code == null){
            return StorageType.UNKNOWN;
        }
        StorageType data = codeLookup.get(code.toLowerCase());
        if (data == null) {
            return StorageType.UNKNOWN;
        }
        return data;
    }
}
