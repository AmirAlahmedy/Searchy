package com.crawler;

public class PageContent {
    private String link;
    private String title;
    private String content;

    public PageContent(String link, String title, String content) {
        this.link = link;
        this.title = title;
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
