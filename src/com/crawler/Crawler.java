package com.crawler;

import com.DbAdapter;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;


public class Crawler implements Runnable{
    private CopyOnWriteArrayList <Pivot> pivotList;
    private DbAdapter db;
    private final int PAGES_TO_CRAWL = 5000;
    private AtomicInteger crawledPages;
    private int backupCrawledPages;
    public List<PageContent> pages;
    private DatabaseReader countryReader;

    private int noThreads;

    public Crawler(CopyOnWriteArrayList<Pivot> pivotList,int noThreads,boolean recrawl) {
//        this.pivotList = pivotList;

        this.noThreads=noThreads;
        db = new DbAdapter();
        try {
            File database = new File("src/com/crawler/GeoLite2-Country.mmdb");
            countryReader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(recrawl){
            deleteOldData();
            crawledPages =new AtomicInteger();
            this.pivotList = pivotList;
            backupCrawledPages = 0;
            return;
        }
        //Setting the country reader

        crawledPages=new AtomicInteger(db.pagesRows());
        //System.out.println(crawledPages.get());
        backupCrawledPages = db.pageBackupCount() + db.pagesRows();
        try {
            ResultSet resultSet = db.getPagesInBackup();
            if (!resultSet.next()) {
                // There is nothing in the crawler backup
                this.pivotList = pivotList;
            }
            else{
                // fill it with the database
                this.pivotList = new CopyOnWriteArrayList<>();
                this.pivotList.add(new Pivot(resultSet.getString("url")));
                while (resultSet.next()){
                    this.pivotList.add(new Pivot(resultSet.getString("url")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    private void crawl(CopyOnWriteArrayList<Pivot> myPivotList , int debugging) {
        if(crawledPages.get() >= PAGES_TO_CRAWL || myPivotList.isEmpty()) return;
        //        for (Pivot a : myPivotList) {
        //            System.out.print(a.getPivot() + " ");
        //            System.out.println();
        //        }

        Document doc;
        PageContent page;

        for(Pivot p : myPivotList) {
            try {
                boolean used =db.isLinkUsedBefore(p.getPivot());

                //Robots r = followRobotExclusionProtocol(p);

                if(!used) {

                    //  Get the robots.txt file
                    Robots r = new Robots(p);

                    //  If robots.txt exists for this website
                    boolean REP = r.followRobotExclusionProtocol();

                    //  disallowed directories from robots.txt
                    CopyOnWriteArrayList <String> disallowedPivotList;

                    if(REP)
                    {
                        sleep(Math.round(r.getCrawlDelay()));
                    }
                    disallowedPivotList = r.getDisallowedPivots();

                    //  if the whole directory is not Disallow: * and the directory is not disallowed ( extra miles in my assumption )
                    if(!r.isDisallowALL() && !disallowedPivotList.contains(p.getPivot())) {
                        doc = Jsoup.connect(p.getPivot()).get();
                        // Gets the title of the web page.
                        String title = doc.title();

                        // Gets the combined text of this element and all its children.
                        String body = doc.body().text();
                        String h1 = doc.select("h1").text();
                        String h2 = doc.select("h2").text();
                        String h3 = doc.select("h3").text();
                        String h4 = doc.select("h4").text();
                        String h5 = doc.select("h5").text();
                        String h6 = doc.select("h6").text();
                        String meta = "";
                        String alt ="";

                        //Getting images srcs and alts to put it in meta and alt
                        Elements imgs = doc.select("img");
                        for (Element el : imgs){
                            if(el.attr("alt") != null && !el.attr("alt").equals("") && el.attr("src") != null && !el.attr("src").equals("")){
                                if(!el.attr("src").startsWith("http"))
                                    continue;
                                alt= alt + el.attr("alt") +"\n\n";
                                meta = meta + el.attr("src") + "\n\n";
                            }
                        }
                        String country = null;
                        //Getting country from ip

                        try {
                            InetAddress ipAddress = InetAddress.getByName(new URL(p.getPivot()).getHost());
                            CountryResponse response = countryReader.country(ipAddress);
                            Country countryResponse = response.getCountry();
                            country = countryResponse.getName();
                            //System.out.println(country);
                        }catch (Exception e){
                            //country will still be null
                            //e.printStackTrace();
                        }

                        String date = "";
                        //Getting date
                        Elements dates = doc.select("[itemprop=datePublished]");
                        for(Element e: dates){
                            if(e.attr("content") != null && !e.attr("content").equals("")) {
                                //splitting and replacing to get the year month day only
                                date = e.attr("content").replaceAll("[-/]", "").split("[T ]")[0];
                                break;
                            }

                            if(e.attr("datetime") != null && !e.attr("datetime").equals("")) {
                                //splitting and replacing to get the year month day only
                                date = e.attr("datetime").replaceAll("[-/]", "").split("[T ]")[0];
                                break;
                            }

                        }
                        // IF there is still no date
                        if(date.equals("")){
                            dates = doc.select("[property=rnews:datePublished]");
                            for(Element e :dates){
                                if(e.attr("content") != null && !e.attr("content").equals("")) {
                                    //splitting and replacing to get the year month day only
                                    date = e.attr("content").replaceAll("[-/]", "").split("[T ]")[0];
                                    break;
                                }
                            }

                            // IF there is still no date
                            if(date.equals("")){
                                dates = doc.select("[property=article:published_time]");
                                for(Element e :dates){
                                    if(e.attr("content") != null && !e.attr("content").equals("")) {
                                        //splitting and replacing to get the year month day only
                                        date = e.attr("content").replaceAll("[-/]", "").split("[T ]")[0];
                                        break;
                                    }
                                }

                                // IF there is still no date
                                if(date.equals("")){
                                    dates = doc.select("[property=article:published_time]");
                                    for(Element e :dates){
                                        if(e.attr("content") != null && !e.attr("content").equals("")) {
                                            //splitting and replacing to get the year month day only
                                            date = e.attr("content").replaceAll("[-/]", "").split("[T ]")[0];
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        int integerDate;
                        try {
                            integerDate = Integer.parseInt(date);
                        } catch (NumberFormatException e) {
                            // Found no date
                            integerDate = 0;
                        }


                        //  int words = title.length() + h1.length() + h2.length() + h3.length() + h4.length() + h5.length() + h6.length() + meta.length() + alt.length() + body.length();
                        boolean done = this.db.addNewPage(p.getPivot(), title, h1, h2, h3, h4, h5, h6, body, alt, meta,integerDate,country);
                        if (done) {
                            crawledPages.incrementAndGet();
                            if(crawledPages.get() >= PAGES_TO_CRAWL ) return;
                            System.out.println("BY THREAD NUMBER "+Thread.currentThread().getName() +" "+ Integer.toString(++debugging));
                        }
                        else{
                            myPivotList.remove(p.getPivot());
                            return;
                        }


                        // 2. Collect all Hyper links within this Doc.
                        Elements links = doc.body().select("a[href]");
                        for (Element link : links) {
                            // Check for disallowed directories
                            Pivot crawled;
                            if(link.attr("href").startsWith("//")){
                                crawled = new Pivot("https:"+link.attr("href"));
                            }
                            else if(link.attr("href").startsWith("/")){
                                crawled = new Pivot(p.pivotRootDirectory()+link.attr("href").substring(1));
                            }
                            else {
                                crawled = new Pivot(link.attr("href"));
                            }
                            if(!crawled.getPivot().startsWith("http"))
                                continue;
                            if(!disallowedPivotList.contains(crawled.getPivot()))
                            {
                                myPivotList.add(crawled);
                                //Add it to backup database
                                if(backupCrawledPages < 3*PAGES_TO_CRAWL) {
                                    db.addPageToBackup(crawled.getPivot());
                                    backupCrawledPages++;
                                }
                            }

                        }
                        //After crawling all the links remove it from the backup database
                        db.removePageFromBackup(p.getPivot());
                        //backupCrawledPages--;
                    }
                }else if(notCrawledYet(p.getPivot())){
                    Robots r = new Robots(p);
                    //  If robots.txt exists for this website
                    boolean REP = r.followRobotExclusionProtocol();
                    //  disallowed directories from robots.txt
                    CopyOnWriteArrayList <String> disallowedPivotList;
                    if(REP)
                    {
                        sleep(Math.round(r.getCrawlDelay()));
                    }
                    disallowedPivotList = r.getDisallowedPivots();
                    //  if the whole directory is not Disallow: * and the directory is not disallowed ( extra miles in my assumption )
                    if(!r.isDisallowALL() && !disallowedPivotList.contains(p.getPivot())) {
                        doc = Jsoup.connect(p.getPivot()).get();
                        Elements links = doc.body().select("a[href]");
                        for (Element link : links) {

                            // Check for disallowed directories
                            Pivot crawled;
                            if(link.attr("href").startsWith("//")){
                                crawled = new Pivot("https:"+link.attr("href"));
                            }
                            else if(link.attr("href").startsWith("/")){
                                crawled = new Pivot(p.pivotRootDirectory()+link.attr("href").substring(1));
                            }
                            else {
                                crawled = new Pivot(link.attr("href"));
                            }
                            if(!crawled.getPivot().startsWith("http"))
                                continue;
                            if (!disallowedPivotList.contains(crawled.getPivot())) {
                                myPivotList.add(crawled);
                                //Add it to backup database
                                if(backupCrawledPages < 3*PAGES_TO_CRAWL) {
                                    db.addPageToBackup(crawled.getPivot());
                                    backupCrawledPages++;
                                }
                            }
                        }
                    }
                    //After crawling all the links remove it from the backup database
                    db.removePageFromBackup(p.getPivot());
                }
                myPivotList.remove(p);
                //TODO: Handle exceptions with descriptive messages.
            } catch (HttpStatusException e) {
                // ignore it
                //e.printStackTrace();
                myPivotList.remove(p);
            }
            catch (SocketException e )
            {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                System.err.println("Bad URL:  " + p.getPivot());
                myPivotList.remove(p);
            } catch (UnknownHostException e) {
                System.err.println("Unable to connect to " + p.getPivot() + " due to weak internet connection.");
            } catch (Exception e){
                //e.printStackTrace();
                myPivotList.remove(p);
            }
        }
        crawl(myPivotList,debugging);

    }
    private boolean notCrawledYet(String url){
        return db.isPageInBackup(url);
    }

    public List<Pivot> getPivotList() {
        return pivotList;
    }

    public void setPivotList(CopyOnWriteArrayList<Pivot> pivotList) {
        this.pivotList = pivotList;
    }

    @Override
    public void run() {
        int threadNumber = Integer.parseInt(Thread.currentThread().getName());
        int pivotsPerThread = pivotList.size() / noThreads;
        System.out.println(threadNumber);

        CopyOnWriteArrayList<Pivot> myPivots = new CopyOnWriteArrayList<>();
        if(threadNumber == noThreads-1){
            for (int j = pivotsPerThread * threadNumber; j < pivotList.size(); j++) {
                myPivots.add(pivotList.get(j));
            }
        } else {
            for (int j = pivotsPerThread * threadNumber; j < pivotsPerThread * (threadNumber + 1); j++) {
                myPivots.add(pivotList.get(j));
            }
        }
        //System.out.println(myPivots.get(0).getPivot());
        crawl(myPivots,0);
    }

    public void deleteOldData(){
        db.deleteAll();
        System.out.println(db.pageBackupCount());
    }

    public static void main(String[] args) throws InterruptedException{

        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
        //SPORTS SEEDS
        pivots.add(new Pivot("https://www.independent.co.uk/"));
        pivots.add(new Pivot("https://www.si.com/"));
        pivots.add(new Pivot("https://www.mirror.co.uk/"));
        //pivots.add(new Pivot("https://www.foxsports.com/"));
        pivots.add(new Pivot("https://www.goal.com/en"));
        pivots.add(new Pivot("https://www.nbcsports.com/"));
        pivots.add(new Pivot("https://www.espn.com/"));
        pivots.add(new Pivot("https://www.theguardian.com/"));
        pivots.add(new Pivot("https://www.bbc.com/"));
        pivots.add(new Pivot("https://www.kingfut.com/"));

        pivots.add(new Pivot("https://www.marca.com/en"));
        //pivots.add(new Pivot("https://www.90min.com/"));
        pivots.add(new Pivot("http://bleacherreport.com/uk"));
        //NEWS SEEDS

        //pivots.add(new Pivot("https://www.bbc.com/news/"));
        pivots.add(new Pivot("https://edition.cnn.com/"));
        //pivots.add(new Pivot("https://www.foxnews.com/"));
        pivots.add(new Pivot("https://www.nbcnews.com/"));
        pivots.add(new Pivot("https://www.nytimes.com/"));
        pivots.add(new Pivot("https://www.dailymail.co.uk/"));
        pivots.add(new Pivot("https://egyptianstreets.com/"));


        // facebook shouldn' t be crawled
        //pivots.add(new Pivot("http://www.facebook.com/"));
        ArrayList<Thread> threadArr=new ArrayList<>();

        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of threads: ");
        int number = input.nextInt();
        input.nextLine();
        System.out.println("Do you want to re-crawl from the beginning y/n? the default is no");
        String recrawl = input.nextLine();
        input.close();

        boolean recrawlFlag = false;
        if(recrawl.equals("y"))
            recrawlFlag = true;
        Runnable crawler = new Crawler(pivots,number,recrawlFlag);

        //Deleting data if I want to recrawl


        for(int i=0;i<number;i++){
            threadArr.add(new Thread(crawler));
            threadArr.get(i).setName(Integer.toString(i));
        }
        for(int i=0;i<number;i++){
            threadArr.get(i).start();
        }

        for(int i=0;i<number;i++){
            threadArr.get(i).join();
        }
    }
}
