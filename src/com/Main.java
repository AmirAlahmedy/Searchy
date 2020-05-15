package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Document document= Jsoup.connect("https://cademartin.com/").get();
        Elements img =document.select("img");
        String alt="";
        for (Element el : img){
            if(el.attr("alt") != null && el.attr("alt") != ""){
                alt= alt + el.attr("alt") +"\t"+el.attr("src")+ "\n";
            }
        }
        System.out.println(alt);

    }

}
