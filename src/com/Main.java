package com;

import com.indexer.Stemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        Document document= Jsoup.connect("https://cademartin.com/").get();
//        Elements img =document.select("img");
//        String alt="";
//        for (Element el : img){
//            if(el.attr("alt") != null && el.attr("alt") != ""){
//                alt= alt + el.attr("alt") +"\t"+el.attr("src")+ "\n";
//            }
//        }
//        System.out.println(alt);

        Stemmer stemmer = new Stemmer();
        String[] words={"hello", "killing", "biggest"};
        for(String token : words){
            System.out.println(stemmer.stem(token));
            char[] word = token.toCharArray();
//            int wordLength = token.length();
//            for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
//            stemmer.stem();
//            System.out.println(stemmer.toString());
        }

    }

}
