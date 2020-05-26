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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws IOException {
//        DbAdapter db = new DbAdapter();
//        Query_Engine qe = new Query_Engine(db);
//        String country="Egypt";
//        qe.processQuery("'The Premier League'",country,false);
//        Document doc = Jsoup.connect("https://www.mirror.co.uk/sport/football/news/premier-leagues-three-key-meetings-22084188").get();
//        Elements elements = doc.select("[itemprop=datePublished]");
//        for(Element e : elements){
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }
//        Document doc2 = Jsoup.connect("https://www.si.com/soccer/2020/05/25/borussia-dortmund-bayern-munich-der-klassiker-bundesliga-preview").get();
//        Elements elements2 = doc2.select("[itemprop=datePublished]");
//        for(Element e: elements2){
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }
//
//
//        Document doc3 = Jsoup.connect("https://www.theguardian.com/football/2020/may/25/world-cup-questions-why-did-england-not-beat-argentina-1998").get();
//        Elements elements3 = doc3.select("[itemprop=datePublished]");
//        for(Element e: elements3){
//            if(e.attr("content").equals(""))
//                System.out.println(e.attr("datetime").replaceAll("[-/]","").split("[T ]")[0]);
//        }

//
//
//        Document doc4 = Jsoup.connect("https://www.bbc.com/sport/football/52801776").get();
//        Elements elements4 = doc4.select("[property=rnews:datePublished]");
//        for(Element e: elements4){
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }

        //Document doc5 = Jsoup.connect("https://www.foxsports.com/nba/story/return-disney-world-adam-silver-covid-coronavirus-052520").get();
        //System.out.println(doc5);
//        Elements elements5 = doc5.select("[itemprop=datePublished]");
//        for(Element e: elements5){
//            //System.out.println(e);
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }

//        Document doc6 = Jsoup.connect("https://www.goal.com/en/news/haaland-limps-off-for-dortmund-in-der-klassiker/2lllobugoxxv1uim12e4vtmo3").get();
//        //System.out.println(doc5);
//        Elements elements6 = doc6.select("[itemprop=datePublished]");
//        for(Element e: elements6){
//            //System.out.println(e);
//            System.out.println(e.attr("dateTime").replaceAll("[-/]","").split("[T ]")[0]);
//        }

//        Document doc7 = Jsoup.connect("https://soccer.nbcsports.com/2020/05/24/crucial-week-ahead-for-the-premier-league/").get();
//        //System.out.println(doc5);
//        Elements elements7 = doc7.select("[property=article:published_time]");
//        for(Element e: elements7){
//            //System.out.println(e);
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }

//        Document doc8 = Jsoup.connect("https://global.espn.com/football/afc-bournemouth/story/4100278/bournemouths-aaron-ramsdale-tests-positive-after-shopping-trip").get();
//        //System.out.println(doc5);
//        Elements elements8 = doc8.select("[name=dc.date.issued]");
//        for(Element e: elements8){
//            //System.out.println(e);
//            System.out.println(e.attr("content").replaceAll("[-/]","").split("[T ]")[0]);
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());
        System.out.println("Delw2tyyyy   "+date);


        String test ="1";
        try {
            System.out.println(Integer.parseInt(test));
        } catch (NumberFormatException e) {
            System.out.println("Ma3lesh");
        }

    }

}
