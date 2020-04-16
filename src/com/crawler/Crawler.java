package com.crawler;

import com.DbAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.html.StyleSheet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Crawler {
    private CopyOnWriteArrayList <Pivot> pivotList;
    private DbAdapter db;

    public Crawler(CopyOnWriteArrayList<Pivot> pivotList) {
        this.pivotList = pivotList;
    }

    private void searchSubPivot(int crawlingDepth) {
        if(crawlingDepth == 0) return;
        Document doc;
        for(Pivot p : pivotList) {
            try {
                // 1. Retrieve a web page (i.e. a document).
                doc = Jsoup.connect(p.getPivot()).get();
                pivotList.remove(p);

                // 2. Collect all links.
                Elements links = doc.body().select("a[href]");
                for(Element link : links) {
                    // TODO: Get rid of the garbage anchor tags like "#" and "sign up pages".

                    // TODO: Either add pages to the database here and edit them after loading the documents
                    // Or load them here and remove the other function

                    //TODO: See if the link already exists in the database before adding
                    pivotList.add(new Pivot(link.attr("href")));
                }
            } catch (IllegalArgumentException e){
                // ignore it
            } catch (MalformedURLException e) {
                System.err.println("Bad URL:  " + p.getPivot());
            } catch (UnknownHostException e) {
                System.err.println("Unable to connect to " + p.getPivot() + " due to weak internet connection.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (final Exception | Error ignored){

            }
        }
        // TODO: Make this function recursive.
        // FIXME: Many bad urls are crawled when recurring.
//        searchSubPivot(crawlingDepth - 1);
        //return;
    }

    public void searchSubPivotContent() {
        //List<PageContent> pages = new ArrayList<>();
        searchSubPivot(5);
        Document doc;
        for (Pivot p : pivotList) {
            try {
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

                this.db.addNewPage(p.getPivot(),title,h1,h2,h3,h4,h5,h6,body,alt,meta);


                //pages.add(new PageContent(p.getPivot(), title, body, h1, h2, h3, h4, h5, h6, meta, alt));

            } catch (IllegalArgumentException e) {
                // For now I will be ignoring the bad urls such as "#" etc..
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public List<Pivot> getPivotList() {
        return pivotList;
    }

    public void setPivotList(CopyOnWriteArrayList<Pivot> pivotList) {
        this.pivotList = pivotList;
    }

    public static void main(String[] args) {

        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
        //pivots.add(new Pivot("http://www.bbc.co.uk/worldservice/africa/2008/11/081124_african_footballer_08_aboutrika.shtml"));
        //pivots.add(new Pivot("http://www.bbc.co.uk/sport"));
        pivots.add(new Pivot("https://www.skysports.com/"));
//        pivots.add(new Pivot("https://www.theguardian.com/uk/sport"));
//        pivots.add(new Pivot("http://bleacherreport.com/uk"));
        //pivots.add(new Pivot("http://www.goal.com/en-gb"));

        Crawler crawler = new Crawler(pivots);
        for(Pivot p : crawler.pivotList)
            System.out.println(p.getPivot());
        //List<PageContent> pages =  crawler.searchSubPivotContent();
        //List<PageContent> pages = new ArrayList<>();
//        String driver="com.mysql.jdbc.Driver";
//        String url="jdbc:mysql://localhost:3306/search_engine";
//        String user="username";
//        try{
//            Class.forName(driver);
//            Connection myConn = DriverManager.getConnection(url,user,null);
//            Statement s=myConn.createStatement();
//        }
//        catch(SQLException | ClassNotFoundException e){
//
//            e.printStackTrace();
//        }
        crawler.db=new DbAdapter();
        crawler.searchSubPivotContent();
    }
}
