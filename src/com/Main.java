package com;

import com.indexer.Stemmer;
import com.query_engine.Query_Engine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
//        DbAdapter db = new DbAdapter();
//        Query_Engine qe = new Query_Engine(db);
//        String country="Egypt";
//        qe.processQuery("'The Premier League'",country,false);
        Document doc = Jsoup.connect("https://www.mirror.co.uk/sport/football/news/premier-leagues-three-key-meetings-22084188").get();
        Elements elements = doc.select("[itemprop=datePublished]");
        for(Element e : elements){
            System.out.println(e.attr("content"));
        }
        Document doc2 = Jsoup.connect("https://www.si.com/soccer/2020/05/25/borussia-dortmund-bayern-munich-der-klassiker-bundesliga-preview").get();
        Elements elements2 = doc2.select("[itemprop=datePublished]");
        for(Element e: elements2){
            System.out.println(e.attr("content"));
        }


        Document doc3 = Jsoup.connect("https://www.theguardian.com/football/2020/may/25/world-cup-questions-why-did-england-not-beat-argentina-1998").get();
        Elements elements3 = doc3.select("[itemprop=datePublished]");
        for(Element e: elements3){
            if(e.attr("content").equals(""))
                System.out.println(e.attr("dateTime"));
        }



    }

}
