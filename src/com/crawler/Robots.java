package com.crawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

class Robots {

    private CopyOnWriteArrayList <String> disallowedPivots;
    private CopyOnWriteArrayList <Pivot> allowedPivots;
    private CopyOnWriteArrayList <Pivot> siteMaps;
    private float crawlDelay = 0;
    private boolean disallowALL;
    private Pivot pivot;
    private Document robotDoc;
    private String URL;

    public Robots(CopyOnWriteArrayList <String> disallowedPivots, CopyOnWriteArrayList <Pivot> allowedPivots, CopyOnWriteArrayList <Pivot> siteMaps, float crawlDelay, boolean disallowALL) {
        this.disallowedPivots = disallowedPivots;
        this.allowedPivots = allowedPivots;
        this.siteMaps = siteMaps;
        this.crawlDelay = crawlDelay;
        this.disallowALL = disallowALL;
    }

    public Robots(Pivot p) {
        this.disallowedPivots = new CopyOnWriteArrayList<String>();
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
            //e.printStackTrace();
            return false;
        }

        String robotFile = this.robotDoc.body().text();

        // Start index of substring
        int startIndex = robotFile.indexOf("User-agent: *");
        if(startIndex == -1) return false;

        // End index of substring
        int endIndex = robotFile.indexOf("User-agent:", startIndex+"User-agent: *".length());

        // if no other User-agent: found
        if (endIndex == -1 ) {
            endIndex = robotFile.length();
        }
        // endIndex = endIndex==-1 ? robotFile.length()-1 : endIndex;

        // ProtocolsBody

        String protocolsBody = robotFile.substring(startIndex, endIndex);
        String[] arrOfProtocols = protocolsBody.split(" ");
        //System.out.println("Length:" + arrOfProtocols.length);


        for (int i=0;i<arrOfProtocols.length;i++)
        {
            if (arrOfProtocols[i].equals("Disallow:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                if ((i+1) == arrOfProtocols.length)
                    return false;
                if(arrOfProtocols[i + 1].equals("/"))   // disallow all
                {
                    this.disallowALL = true;
                }
                if( arrOfProtocols[i + 1].equals(" ") || arrOfProtocols[i + 1].equals("") || arrOfProtocols[i + 1].equals("\n") || arrOfProtocols[i + 1].equals(" \n"))  // allow all
                {
                    return false;
                }
                String disallowedURL;
                if(arrOfProtocols[i+1].startsWith("/"))
                    disallowedURL = new String(this.URL + arrOfProtocols[i+1].substring(1));
                else
                    disallowedURL = new String(this.URL + arrOfProtocols[i + 1]);

                this.disallowedPivots.add(disallowedURL);
            } else if (arrOfProtocols[i].equals("Allow:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                Pivot allowedURL = new Pivot(this.URL + arrOfProtocols[i + 1].substring(arrOfProtocols[i + 1].indexOf("/")+1,arrOfProtocols[i + 1].length()));
                this.allowedPivots.add(allowedURL);
            } else if (arrOfProtocols[i].equals("Sitemap:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                Pivot siteMap = new Pivot(this.URL + arrOfProtocols[i + 1].substring(arrOfProtocols[i + 1].indexOf("/")+1,arrOfProtocols[i + 1].length()));
                this.siteMaps.add(siteMap);
            } else if (arrOfProtocols[i].equals("Crawl-delay:")) {
                //System.out.println(arrOfProtocols[i] + "" + arrOfProtocols[i + 1]);
                try {
                    this.crawlDelay = Float.parseFloat(arrOfProtocols[i + 1]);
                }catch (NumberFormatException e){
                }
            }
            else{
                //  DO NOTHING
            }
        }
        return true;
    }
    public CopyOnWriteArrayList<String> getDisallowedPivots() { return disallowedPivots; }

    //public void setDisallowedPivots(CopyOnWriteArrayList<Pivot> disallowedPivots) { this.disallowedPivots = disallowedPivots; }

    public CopyOnWriteArrayList<Pivot> getAllowedPivots() { return allowedPivots; }

    //public void setAllowedPivots(CopyOnWriteArrayList<Pivot> allowedPivots) { this.allowedPivots = allowedPivots; }

    public CopyOnWriteArrayList<Pivot> getSiteMaps() { return siteMaps; }

    //public void setSiteMaps(CopyOnWriteArrayList<Pivot> siteMaps) { this.siteMaps = siteMaps; }

    public float getCrawlDelay() { return crawlDelay; }

    //public void setCrawlDelay(int crawlDelay) { this.crawlDelay = crawlDelay; }

    public boolean isDisallowALL() { return disallowALL; }

    //public void setDisallowALL(boolean disallowALL) { this.disallowALL = disallowALL; }

}
