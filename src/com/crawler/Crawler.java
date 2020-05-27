package com.crawler;

import com.DbAdapter;
//import javafx.scene.input.PickResult;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
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

    private int noThreads;

    public Crawler(CopyOnWriteArrayList<Pivot> pivotList,int noThreads,boolean recrawl) {
//        this.pivotList = pivotList;
        this.noThreads=noThreads;
        db = new DbAdapter();
        if(recrawl){
            deleteOldData();
            crawledPages =new AtomicInteger();
            this.pivotList = pivotList;
            backupCrawledPages = 0;
            return;
        }
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



    private void crawl(CopyOnWriteArrayList<Pivot> myPivotList) {
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
                        //  Add allowed Pivots from robots.txt
                        myPivotList.addAllAbsent(r.getAllowedPivots());
                        // Adding them to the crawler backup
                        for(Pivot temp : r.getAllowedPivots()){
                            if(backupCrawledPages < PAGES_TO_CRAWL) {
                                db.addPageToBackup(temp.getPivot());
                                backupCrawledPages++;
                            }
                        }
                        //  Apply the specified delay from robots.txt
                        sleep(r.getCrawlDelay());
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
                                alt= alt + el.attr("alt") +"\n\n";
                                meta = meta + el.attr("src") + "\n\n";
                            }
                        }
                        String country = null;
                        String date = "";
                        //Getting date
                        Elements dates = doc.select("[itemprop=datePublished]");
                        for(Element e: dates){
                            if(e.attr("content") != null && !e.attr("content").equals(""))
                                //splitting and replacing to get the year month day only
                                date = e.attr("content").replaceAll("[-/]","").split("[T ]")[0];

                            if(e.attr("datetime") != null && !e.attr("datetime").equals(""))
                                //splitting and replacing to get the year month day only
                                date = e.attr("datetime").replaceAll("[-/]","").split("[T ]")[0];

                        }
                        // IF there is still no date
                        if(date.equals("")){
                            dates = doc.select("[property=rnews:datePublished]");
                            for(Element e :dates){
                                if(e.attr("content") != null && !e.attr("content").equals(""))
                                    //splitting and replacing to get the year month day only
                                    date = e.attr("content").replaceAll("[-/]","").split("[T ]")[0];
                            }

                            // IF there is still no date
                            if(date.equals("")){
                                dates = doc.select("[property=article:published_time]");
                                for(Element e :dates){
                                    if(e.attr("content") != null && !e.attr("content").equals(""))
                                        //splitting and replacing to get the year month day only
                                        date = e.attr("content").replaceAll("[-/]","").split("[T ]")[0];
                                }

                                // IF there is still no date
                                if(date.equals("")){
                                    dates = doc.select("[property=article:published_time]");
                                    for(Element e :dates){
                                        if(e.attr("content") != null && !e.attr("content").equals(""))
                                            //splitting and replacing to get the year month day only
                                            date = e.attr("content").replaceAll("[-/]","").split("[T ]")[0];
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
                        }
                            //  this.db.addNewPage(page);
                            //  if(!pages.contains(page)) {
                            //      pages.add(new PageContent(p.getPivot(), title, body, h1, h2, h3, h4, h5, h6, meta, alt));
                            //  }

                        // 2. Collect all Hyper links within this Doc.
                        Elements links = doc.body().select("a[href]");
                        for (Element link : links) {
                            // Check for disallowed directories
                            Pivot crawled = new Pivot(link.attr("href"));
                            if(!crawled.getPivot().startsWith("http"))
                                continue;
                            if(!disallowedPivotList.contains(crawled.getPivot()))
                            {
                                myPivotList.add(crawled);
                                //Add it to backup database
                                if(backupCrawledPages < PAGES_TO_CRAWL) {
                                    db.addPageToBackup(crawled.getPivot());
                                    backupCrawledPages++;
                                }
                            }

                            //  TODO: Get rid of the garbage anchor tags like "#" and "sign up pages".

                            //TODO: Either add pages to the database here and edit them after loading the documents
                            // Or load them here and remove the other function

                            //TODO: See if the link already exists in the database before adding
                            // If it does not exist in the database add it, otherwise update it.

                        }
                        //After crawling all the links remove it from the backup database
                        db.removePageFromBackup(p.getPivot());
                    }
                }else if(notCrawledYet(p.getPivot())){
                    Robots r = new Robots(p);
                    //  If robots.txt exists for this website
                    boolean REP = r.followRobotExclusionProtocol();
                    //  disallowed directories from robots.txt
                    CopyOnWriteArrayList <String> disallowedPivotList;
                    if(REP)
                    {
                        //  Add allowed Pivots from robots.txt
                        myPivotList.addAllAbsent(r.getAllowedPivots());
                        for(Pivot temp : r.getAllowedPivots()){
                            if(backupCrawledPages < PAGES_TO_CRAWL) {
                                db.addPageToBackup(temp.getPivot());
                                backupCrawledPages++;
                            }
                        }
                        //  Apply the specified delay from robots.txt
                        sleep(r.getCrawlDelay());
                    }
                    disallowedPivotList = r.getDisallowedPivots();
                    //  if the whole directory is not Disallow: * and the directory is not disallowed ( extra miles in my assumption )
                    if(!r.isDisallowALL() && !disallowedPivotList.contains(p.getPivot())) {
                        doc = Jsoup.connect(p.getPivot()).get();
                        Elements links = doc.body().select("a[href]");
                        for (Element link : links) {

                            // Check for disallowed directories
                            Pivot crawled = new Pivot(link.attr("href"));
                            if(!crawled.getPivot().startsWith("http"))
                                continue;
                            if (!disallowedPivotList.contains(crawled.getPivot())) {
                                myPivotList.add(crawled);
                                //Add it to backup database
                                if(backupCrawledPages < PAGES_TO_CRAWL) {
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
                myPivotList.remove(p);
            }
            catch (SocketException e )
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                // ignore it
            } catch (MalformedURLException e) {
                System.err.println("Bad URL:  " + p.getPivot());
                myPivotList.remove(p);
            } catch (UnknownHostException e) {
                System.err.println("Unable to connect to " + p.getPivot() + " due to weak internet connection.");
            } catch( UnsupportedMimeTypeException e){
                myPivotList.remove(p);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                myPivotList.remove(p);
            }
//            } catch (final Exception | Error ignored){
//                myPivotList.remove(p);
//            }
        }
        //FIXME: Many bad urls are crawled when recurring.
        crawl(myPivotList);

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
        //System.out.println(noThreads);
        System.out.println(threadNumber);

        for (int i = 0; i < noThreads; i++) {
            CopyOnWriteArrayList<Pivot> myPivots = new CopyOnWriteArrayList<>();
            if (threadNumber == i) {
                if(threadNumber == noThreads-1){
                    for (int j = pivotsPerThread * i; j < pivotList.size(); j++) {
                        myPivots.add(pivotList.get(j));
                    }
                } else {
                    for (int j = pivotsPerThread * i; j < pivotsPerThread * (i + 1); j++) {
                        myPivots.add(pivotList.get(j));
                    }
                }
                //System.out.println(myPivots.get(0).getPivot());
                crawl(myPivots);
            }

        }
    }

    public void deleteOldData(){
        db.deleteAll();
        System.out.println(db.pageBackupCount());
    }

    public static void main(String[] args) throws InterruptedException{

        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
        pivots.add(new Pivot("https://uk.sports.yahoo.com/football/?guccounter=1"));
        pivots.add(new Pivot("https://www.independent.co.uk/sport/football"));
        pivots.add(new Pivot("https://www.si.com/soccer"));
        pivots.add(new Pivot("https://www.mirror.co.uk/sport/football/"));
        pivots.add(new Pivot("https://www.90min.com/"));
        pivots.add(new Pivot("https://www.foxsports.com/"));
        pivots.add(new Pivot("https://www.goal.com/en"));
        pivots.add(new Pivot("https://www.nbcsports.com/"));
        pivots.add(new Pivot("https://global.espn.com/football/?src=com"));
        pivots.add(new Pivot("https://www.theguardian.com/football"));
        pivots.add(new Pivot("http://bleacherreport.com/uk"));
        pivots.add(new Pivot("https://www.skysports.com/football"));
        pivots.add(new Pivot("https://www.bbc.com/sport/football"));
        // facebook shouldn' t be crawled
        //pivots.add(new Pivot("http://www.facebook.com/"));
        ArrayList<Thread> threadArr=new ArrayList<>();
//        pivots.add(new Pivot("https://www.minutemedia.com/careers"));

        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of threads: ");
        int number = input.nextInt();
        input.nextLine();
        System.out.println("Do you want to re-crawl from the beginning y/n? the default is no");
        String recrawl = input.nextLine();
        input.close();

        //if the number of threads is more than the seeds size this will be not useful
//        if(number > pivots.size()){
//            number = pivots.size();
//        }
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
