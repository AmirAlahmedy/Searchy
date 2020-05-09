package com.indexer;

import com.DbAdapter;
import com.crawler.Crawler;
import com.crawler.Pivot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class InvertedIndex {
    private ResultSet resultSet;
    private List<String> stopWords = Collections.emptyList();
    private DbAdapter dbAdapter;

    // Constructor of the inverted index of a specific page.
    public InvertedIndex() {
        this.dbAdapter =  new DbAdapter();
        this.resultSet = this.dbAdapter.readPages();
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Returns a list of terms, which are going to be the keys in the invertedIndex.
    public void parseCollection() throws SQLException {

        while (resultSet.next()) {
            int pageId = resultSet.getInt("id");
            int words = resultSet.getInt("words");
            String parsedContent;

            // Depends on the order of the columns in the pages table.
            for (int i = 3; i <= 11; ++i) {
                parsedContent = resultSet.getString(i);

                // 1. Lowercase all words.
                parsedContent = parsedContent.toLowerCase();

                if (i == 11) {         // to extract the the image alt and src only
                    String [] srcs = resultSet.getString(12).split(" ");
                    String[] alts = parsedContent.split("\n");
                    int counter = 0;
                    for (String img : alts){
                        if(!img.equals("")) {
                            if(!srcs[counter].contains("http")){
                                counter = counter + 1;
                                continue;
                            }
                            // the first element is the alt and the second is the src
                            img = img.replaceAll("[^a-z0-9]", " ");
                            List<String> tokens = Arrays.asList(img.split("[^a-z0-9]"));
                            try {
                                Stemmer stemmer = new Stemmer();
                                for (String token : tokens) {
                                    if (!token.equals("") && !stopWords.contains(token)) {
                                        char[] word = token.toCharArray();
                                        int wordLength = token.length();
                                        for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
                                        stemmer.stem();
                                        dbAdapter.addNewImg(pageId,stemmer.toString(),srcs[counter]);

                                    }
                                }
                                //t2=System.currentTimeMillis();
                                //System.out.println(t2-t1);
                                counter = counter +1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                } else {

                    // 2. Get all alphanumeric tokens.
                    parsedContent = parsedContent.replaceAll("[^a-z0-9]", " ");

                    //long t1,t2;
                    //t1=System.currentTimeMillis();


                    // 3. Filter out stop words and stem each token.
                    // Extract tokens from the page content into a list.
                    List<String> tokens = Arrays.asList(parsedContent.split("[^a-z0-9]"));
                    //ArrayList<String> stems = new ArrayList<>();

                    try {
                        Stemmer stemmer = new Stemmer();
                        for (String token : tokens) {
                            if (!token.equals("") && !stopWords.contains(token)) {
                                char[] word = token.toCharArray();
                                int wordLength = token.length();
                                for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
                                stemmer.stem();
                                dbAdapter.addNewTerm(stemmer.toString(), pageId, i, words);
                            }
                        }
                        //t2=System.currentTimeMillis();
                        //System.out.println(t2-t1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    public static void main(String[] args) throws SQLException {

//        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
//        pivots.add(new Pivot("https://en.wikipedia.org/wiki/Augmented_reality"));
//
//        Crawler crawler = new Crawler(pivots, 1);
//        crawler.crawl();


        InvertedIndex indexer = new InvertedIndex();
        indexer.parseCollection();
    }

}
