package com.crawler;

import com.DbAdapter;
//import javafx.scene.input.PickResult;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    public List<PageContent> pages;

    private int noThreads;

    public Crawler(CopyOnWriteArrayList<Pivot> pivotList,int noThreads) {
        this.pivotList = pivotList;
        this.noThreads=noThreads;
        db = new DbAdapter();
        crawledPages=new AtomicInteger();
        //this.pages = new ArrayList<>();
    }

    /*private Robots followRobotExclusionProtocol(Pivot p)
    {
        try {
            String URL = p.getPivot().substring(0,p.getPivot().indexOf('/',"https://".length())) + "/";
            System.out.println(URL+"robots.txt");
            Document robotDoc = Jsoup.connect(URL + "robots.txt").get();
            String robotFile = robotDoc.body().text();
            int startIndex = robotFile.indexOf("User-agent: *");
            if(startIndex == -1) return null;
            CopyOnWriteArrayList <Pivot> disallowedPivots = new CopyOnWriteArrayList<Pivot>();
            CopyOnWriteArrayList <Pivot> allowedPivots = new CopyOnWriteArrayList<Pivot>();
            CopyOnWriteArrayList <Pivot> siteMaps = new CopyOnWriteArrayList<Pivot>();
            int crawlDelay = 0;
            boolean disallowAll = false;
            int endIndex = robotFile.indexOf("User-agent:", startIndex+"User-agent:".length());
            endIndex = endIndex==-1 ? robotFile.length()-1 : endIndex;  // if no other User-agent: found
            String protocolsBody = robotFile.substring(startIndex, endIndex);
            String[] arrOfProtocols = protocolsBody.split(" ");
            System.out.println(arrOfProtocols.length);
            for (int i=0;i<arrOfProtocols.length;i+=2)
            {
                System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                if (arrOfProtocols[i].equals("Disallow:")) {
                    if(arrOfProtocols[i + 1].equals("/"))   // disallow all
                    {
                        disallowAll = true;
                    }
                    Pivot disallowedURL = new Pivot(URL + arrOfProtocols[i + 1]);
                    disallowedPivots.add(disallowedURL);
                } else if (arrOfProtocols[i].equals("Allow:")) {
                    Pivot allowedURL = new Pivot(URL + arrOfProtocols[i + 1]);
                    allowedPivots.add(allowedURL);
                } else if (arrOfProtocols[i].equals("Sitemap:")) {
                    Pivot siteMap = new Pivot(URL + arrOfProtocols[i + 1]);
                    siteMaps.add(siteMap);
                } else if (arrOfProtocols[i].equals("Crawl-delay:")) {
                    crawlDelay = Integer.parseInt(arrOfProtocols[i + 1]);
                }
                else{
                    //  DO NOTHING
                }
            }
            Robots r = new Robots(disallowedPivots,allowedPivots,siteMaps,crawlDelay,disallowAll);
            return r;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    private void crawl(CopyOnWriteArrayList<Pivot> myPivotList) {
        if(crawledPages.get() == PAGES_TO_CRAWL || myPivotList.isEmpty()) return;
        Document doc;
        PageContent page;

        for(Pivot p : myPivotList) {
            try {
                // 1. Retrieve a web page (i.e. a document).
                boolean used =db.isLinkUsedBefore(p.getPivot());

                //Robots r = followRobotExclusionProtocol(p);

                if(!used) {

                    //  Get the robots.txt file
                    //Robots r = new Robots(new Pivot("https://www.geeksforgeeks.org/"));

                    Robots r = new Robots(p);
                    boolean REP = r.followRobotExclusionProtocol();
                    if(REP)
                    {
                        myPivotList.removeAll(r.getDisallowedPivots());
                        myPivotList.addAll(r.getAllowedPivots());
                        myPivotList.addAll(r.getSiteMaps());
                        sleep(r.getCrawlDelay());
                    }


                    if(!r.isDisallowALL()) {
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
                        String meta = doc.select("meta").text();
                        String alt = doc.select("alt").text();

                        int words = title.length() + h1.length() + h2.length() + h3.length() + h4.length() + h5.length() + h6.length() + meta.length() + alt.length() + body.length();
                        boolean done = this.db.addNewPage(p.getPivot(), title, h1, h2, h3, h4, h5, h6, body, alt, meta, words);
                        if (done) {
                            crawledPages.incrementAndGet();
                        }
                        //this.db.addNewPage(page);
//                if(!pages.contains(page)) {
//                    pages.add(new PageContent(p.getPivot(), title, body, h1, h2, h3, h4, h5, h6, meta, alt));
//                }

                        // 2. Collect all Hyper links within this Doc.
                        Elements links = doc.body().select("a[href]");
                        for (Element link : links) {
                            //TODO: Get rid of the garbage anchor tags like "#" and "sign up pages".

                            //TODO: Either add pages to the database here and edit them after loading the documents
                            // Or load them here and remove the other function

                            //TODO: See if the link already exists in the database before adding
                            // If it does not exist in the database add it, otherwise update it.
                            myPivotList.add(new Pivot(link.attr("href")));
                            //myPivotList.removeAll()
//                    page = new PageContent(link.attr("href"), link.select("title").text(), link.select("body").text(), link.select("h1").text(), link.select("h2").text(), link.select("h3").text(), link.select("h4").text(), link.select("h5").text(), link.select("h6").text(), link.select("meta").text(), link.select("alt").text());
//                    if (!pages.contains(page)){
//                        pivotList.add(new Pivot(link.attr("href")));
//                    }
                        }
                    }
                }
                myPivotList.remove(p);
                //TODO: Handle exceptions with descriptive messages.
            } catch (HttpStatusException e) {
                // ignore it
            }
            catch (SocketException e )
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                // ignore it
            } catch (MalformedURLException e) {
                System.err.println("Bad URL:  " + p.getPivot());
            } catch (UnknownHostException e) {
                System.err.println("Unable to connect to " + p.getPivot() + " due to weak internet connection.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (final Exception | Error ignored){

            }
        }
        //FIXME: Many bad urls are crawled when recurring.
        crawl(myPivotList);

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
                System.out.println(myPivots.get(0).getPivot());
                crawl(myPivots);
            }

        }
    }

    public static void main(String[] args) throws InterruptedException{

        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
        pivots.add(new Pivot("http://www.bbc.co.uk/worldservice/afric2a/2008/11/081124_african_footballer_08_aboutrika.shtml"));
        pivots.add(new Pivot("https://www.skysports.com/"));
        pivots.add(new Pivot("http://www.espn.com/"));

        pivots.add(new Pivot("https://www.pinterest.com/"));
        pivots.add(new Pivot("https://www.theguardian.com/uk/sport"));
        pivots.add(new Pivot("http://bleacherreport.com/uk"));
        pivots.add(new Pivot("http://www.goal.com/en-gb"));
        ArrayList<Thread> threadArr=new ArrayList<>();

        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of threads: ");
        int number = input.nextInt();
        input.close();
        Runnable crawler = new Crawler(pivots,number);
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
