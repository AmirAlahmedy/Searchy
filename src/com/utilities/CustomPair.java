package com.utilities;

public class CustomPair {
    private Integer key;
    private Integer value;

    public CustomPair(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
