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

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String date = simpleDateFormat.format(new Date());
//        System.out.println("Delw2tyyyy   "+date);
//
//
//        String test ="1";
//        try {
//            System.out.println(Integer.parseInt(test));
//        } catch (NumberFormatException e) {
//            System.out.println("Ma3lesh");
//        }

        Document test2 = Jsoup.connect("https://uk.sports.yahoo.com/news/tottenham-complete-bergwijn-signing-104727841.html").get();
        System.out.println(test2.body().text());
    }

}
