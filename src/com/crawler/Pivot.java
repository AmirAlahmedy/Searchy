package com.crawler;

public class Pivot {
    private String pivot;
    public Pivot(String pivot) {
        this.pivot = pivot;
    }

    public void setPivot(String pivot) {
        this.pivot = pivot;
    }

    public String getPivot() {
        return pivot;
    }

    public String pivotRootDirectory() {
        int i = pivot.indexOf('/',"https://".length());
        if(i == -1){
            return pivot+"/";
        }
        else{
            return pivot.substring(0,i) + "/";
        }
    }
}
