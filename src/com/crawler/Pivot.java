package com.crawler;

public class Pivot {
    private String pivot;
    public Pivot(String pivot) {
        if(!pivot.endsWith("/"))
            pivot = pivot + "/";
        this.pivot = pivot;
    }

    public void setPivot(String pivot) {
        this.pivot = pivot;
    }

    public String getPivot() {
        return pivot;
    }

    public String pivotRootDirectory() {
        return pivot.substring(0,pivot.indexOf('/',"https://".length())) + "/";
    }
}
