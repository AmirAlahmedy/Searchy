package com;

import com.indexer.Stemmer;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import com.query_engine.Query_Engine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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

//        Document doc = Jsoup.connect("https://www.si.com/soccer/2020/05/27/fake-crowd-noise-soccer-tv-broadcasts-bundesliga").get();
//        System.out.println(doc.select("p").text().length()
//                + doc.select("li").text().length()
//                + doc.select("ol").text().length()
//                + doc.select("td").text().length()
//                + doc.select("th").text().length()
//                + doc.select("ul").text().length());
//        System.out.println("bodyyyy");
//        System.out.println( doc.body().text().length());

        File database = new File("src/com/crawler/GeoLite2-Country.mmdb");
        DatabaseReader reader = new DatabaseReader.Builder(database).build();
        try {
            InetAddress ipAddress = InetAddress.getByName("www.independent.co.uk");
            //System.out.println(ipAddress.toString());
            System.out.println(ipAddress.toString());
            CountryResponse response = reader.country(ipAddress);
            Country country = response.getCountry();
            System.out.println(country.getName());

        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }


    }

}
