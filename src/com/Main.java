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
        Document document= Jsoup.connect("https://cademartin.com/").get();
        Elements img =document.select("img");
        String alt="";
        for (Element el : img){
            if(el.attr("alt") != null && el.attr("alt") != ""){
                alt= alt + el.attr("alt") +"\t"+el.attr("src")+ "\n";
            }
        }
        System.out.println(alt);

    }

}
