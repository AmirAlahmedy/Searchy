package com;

import com.crawler.Engine;
import com.crawler.PageContent;
import com.crawler.Pivot;
import com.utilities.CustomPair;
import com.utilities.MultiMap;
import com.indexer.InvertedIndex;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Pivot> pivots = new ArrayList<>();
        pivots.add(new Pivot("https://www.wikipedia.org/"));
        pivots.add(new Pivot("https://www.youtube.com/"));
        pivots.add(new Pivot("https://www.baeldung.com/"));
        pivots.add(new Pivot("https://developer.mozilla.org/en-US/"));
        pivots.add(new Pivot("https://www.quora.com/"));
        pivots.add(new Pivot("https://www.google.com/search?client=ubuntu&channel=fs&q=roger+penrose&ie=utf-8&oe=utf-8"));

        Engine engine = new Engine(pivots);
        List<PageContent> pages =  engine.searchSubPivotContent();

        InvertedIndex pageIndexer;
        ArrayList<String> pageWords;
        MultiMap<String, Integer> postings = new MultiMap<>();

        Integer pageId = 0, wordCount;
        for(PageContent pg : pages) {
            System.out.println(pg.getTitle());
            System.out.println("------------------------------------------------------");
            System.out.println(pg.getLink());
            System.out.println(pg.getContent());
            System.out.println("------------------------------------------------------");

            pageIndexer = new InvertedIndex(pg);
            pageWords = pageIndexer.parseCollection();

            wordCount = 1;
            for(String word : pageWords){
                if(postings.containsKey(word)) wordCount++;
                postings.putIfAbsent(word, pageId);
            }
            pageId++;
        }

        System.out.println("----- Printing Multimap using keySet -----\n");
        for (String term : postings.keySet()) {
            System.out.println(term + ": " + postings.get(term));
        }

    }
}
