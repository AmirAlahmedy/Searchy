package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Robots {

    private CopyOnWriteArrayList<Pivot> disallowedPivots;
    private CopyOnWriteArrayList <Pivot> allowedPivots;
    private CopyOnWriteArrayList <Pivot> siteMaps;
    private int crawlDelay = 0;
    private boolean disallowALL;
    private Pivot pivot;
    private Document robotDoc;
    private String URL;

    public Robots(CopyOnWriteArrayList<Pivot> disallowedPivots, CopyOnWriteArrayList <Pivot> allowedPivots, CopyOnWriteArrayList <Pivot> siteMaps, int crawlDelay, boolean disallowALL) {
        this.disallowedPivots = disallowedPivots;
        this.allowedPivots = allowedPivots;
        this.siteMaps = siteMaps;
        this.crawlDelay = crawlDelay;
        this.disallowALL = disallowALL;
    }

    public Robots(Pivot p) {
        this.disallowedPivots = new CopyOnWriteArrayList<Pivot>();
        this.allowedPivots = new CopyOnWriteArrayList<Pivot>();
        this.siteMaps = new CopyOnWriteArrayList<Pivot>();
        int crawlDelay = 0;
        boolean disallowAll = false;
        pivot = p;
        URL = p.pivotRootDirectory();
    }

    public boolean followRobotExclusionProtocol()
    {

        try {
            this.robotDoc = Jsoup.connect(this.URL + "robots.txt").get();
        }
        catch (IOException e) { // If 404 throws exception therefore return false
            e.printStackTrace();
            return false;
        }

        String robotFile = this.robotDoc.body().text();

        // Start index of substring
        int startIndex = robotFile.indexOf("User-agent: *");
        if(startIndex == -1) return false;

        // End index of substring
        int endIndex = robotFile.indexOf("User-agent:", startIndex+"User-agent:".length());
        endIndex = endIndex==-1 ? robotFile.length()-1 : endIndex;  // if no other User-agent: found

        // ProtocolsBody
        String protocolsBody = robotFile.substring(startIndex, endIndex);
        String[] arrOfProtocols = protocolsBody.split(" ");
        //System.out.println(arrOfProtocols.length);


        for (int i=0;i<arrOfProtocols.length;i++)
        {
            if (arrOfProtocols[i].equals("Disallow:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                if(arrOfProtocols[i + 1].equals("/"))   // disallow all
                {
                    this.disallowALL = true;
                }
                if( (i+1)==arrOfProtocols.length || arrOfProtocols[i + 1].equals(" ") || arrOfProtocols[i + 1].equals(""))  // allow all
                {
                    return false;
                }
                Pivot disallowedURL = new Pivot(this.URL + arrOfProtocols[i + 1]);
                this.disallowedPivots.add(disallowedURL);
            } else if (arrOfProtocols[i].equals("Allow:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                Pivot allowedURL = new Pivot(this.URL + arrOfProtocols[i + 1]);
                this.allowedPivots.add(allowedURL);
            } else if (arrOfProtocols[i].equals("Sitemap:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                Pivot siteMap = new Pivot(this.URL + arrOfProtocols[i + 1]);
                this.siteMaps.add(siteMap);
            } else if (arrOfProtocols[i].equals("Crawl-delay:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                this.crawlDelay = Integer.parseInt(arrOfProtocols[i + 1]);
            }
            else{
                //  DO NOTHING
            }
        }
        return true;
    }
    public CopyOnWriteArrayList<Pivot> getDisallowedPivots() { return disallowedPivots; }

    //public void setDisallowedPivots(CopyOnWriteArrayList<Pivot> disallowedPivots) { this.disallowedPivots = disallowedPivots; }

    public CopyOnWriteArrayList<Pivot> getAllowedPivots() { return allowedPivots; }

    //public void setAllowedPivots(CopyOnWriteArrayList<Pivot> allowedPivots) { this.allowedPivots = allowedPivots; }

    public CopyOnWriteArrayList<Pivot> getSiteMaps() { return siteMaps; }

    //public void setSiteMaps(CopyOnWriteArrayList<Pivot> siteMaps) { this.siteMaps = siteMaps; }

    public int getCrawlDelay() { return crawlDelay; }

    //public void setCrawlDelay(int crawlDelay) { this.crawlDelay = crawlDelay; }

    public boolean isDisallowALL() { return disallowALL; }

    //public void setDisallowALL(boolean disallowALL) { this.disallowALL = disallowALL; }

}
