package com;

import com.crawler.Crawler;
import com.crawler.PageContent;
import com.crawler.Pivot;
import com.utilities.MultiMap;
import com.indexer.InvertedIndex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        Document document= Jsoup.connect("https://en.wikipedia.org/wiki/Portal:Sports").get();
       // Elements img =document.select("img");
        System.out.println(document.title());

    }

}
