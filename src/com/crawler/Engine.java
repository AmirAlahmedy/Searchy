package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Engine {
    private List<Pivot> pivotList;

    public Engine(List<Pivot> pivotList) {
        this.pivotList = pivotList;
    }

    private List<Pivot> searchSubPivot() {
        List<Pivot> urls = new ArrayList<>();
        Document doc;
        for(Pivot p : pivotList) {
            try {
                // 1. Retrieve a web page (i.e. a document).
                doc = Jsoup.connect(p.getPivot()).get();

                // 2. Collect all links.
                Elements links = doc.select("a[href]");
                for(Element link : links) {
                    // TODO: Get rid of the garbage anchor tags like "#" and "sign up pages".
                    urls.add(new Pivot(link.attr("href")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return urls;
    }

    public List<PageContent> searchSubPivotContent() {
        List<PageContent> pages = new ArrayList<>();
        List<Pivot> pivots = searchSubPivot();
        Document doc;
        for (Pivot p : pivots) {
            try {
                doc = Jsoup.connect(p.getPivot()).get();

                // Gets the title of the web page.
                String title = doc.title();

                // Gets the combined text of this element and all its children.
                String content = doc.body().text();

                pages.add(new PageContent(p.getPivot(), title, content));

            } catch (IllegalArgumentException e) {
                // For now I will be ignoring the bad urls such as "#" etc..
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pages;
    }

    public List<Pivot> getPivotList() {
        return pivotList;
    }

    public void setPivotList(List<Pivot> pivotList) {
        this.pivotList = pivotList;
    }
}
