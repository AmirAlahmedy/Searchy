package com.crawler;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Pivot> pivots = new ArrayList<>();
        pivots.add(new Pivot("https://stackoverflow.com"));

        Engine engine = new Engine(pivots);
        List<PageContent> pages =  engine.searchSubPivotContent();
        for(PageContent pg : pages) {
            System.out.println(pg.getTitle());
            System.out.println("------------------------------------------------------");
            System.out.println(pg.getLink());
            System.out.println(pg.getContent());
            System.out.println("######################################################");
        }
    }
}
