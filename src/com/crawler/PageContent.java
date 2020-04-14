package com.crawler;

import java.util.concurrent.atomic.AtomicInteger;

public class PageContent {
    private static AtomicInteger counter= new AtomicInteger(0);
    private int id;

    private String link;
    private String title;
    private String body;
    // Header tags.
    private String h1;
    private String h2;
    private String h3;
    private String h4;
    private String h5;
    private String h6;
    // Meta description tag.
    private String meta;
    private String alt;

    public PageContent(String link, String title, String body, String h1, String h2, String h3, String h4, String h5, String h6, String meta, String alt) {
        this.link = link;
        this.title = title;
        this.body = body;
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
        this.h4 = h4;
        this.h5 = h5;
        this.h6 = h6;
        this.meta = meta;
        this.alt = alt;

        this.id =counter.incrementAndGet();
    }

    public int getId() { return id; }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getH2() {
        return h2;
    }

    public void setH2(String h2) {
        this.h2 = h2;
    }

    public String getH3() {
        return h3;
    }

    public void setH3(String h3) {
        this.h3 = h3;
    }

    public String getH4() {
        return h4;
    }

    public void setH4(String h4) {
        this.h4 = h4;
    }

    public String getH5() {
        return h5;
    }

    public void setH5(String h5) {
        this.h5 = h5;
    }

    public String getH6() {
        return h6;
    }

    public void setH6(String h6) {
        this.h6 = h6;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
