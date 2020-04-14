package com.indexer;

import com.crawler.PageContent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InvertedIndex {
    private HashMap<Integer, List<Integer>> postings;
    private PageContent page = null;
    //private final String stopWords = "A | a |The | the |An | an | of | but | at | therefore | be | can | also | they | is ";
    private List<String> stopWords = Collections.emptyList();
    // Constructor of the main inverted index.
    public InvertedIndex(HashMap<Integer, List<Integer>> postings) {
        this.postings = postings;
    }

    // Constructor of the inverted index of a specific page.
    public InvertedIndex(PageContent page) {
        this.page = page;
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Returns a list of terms, which are going to be the keys in the invertedIndex.
    public ArrayList<String> parseCollection() {
        String parsedContent = "";

        // 1. Concatenate the title and the text of the page.
        parsedContent = page.getTitle() +" "+ page.getBody();

        // 2. Lowercase all words.
        parsedContent = parsedContent.toLowerCase();

        // 3. Get all alphanumeric tokens.
        parsedContent = parsedContent.replaceAll("[^a-z0-9]", " ");


        //long t1,t2;
        //t1=System.currentTimeMillis();

        // 4. Filter out stop words.
        //parsedContent = parsedContent.replaceAll(stopWords , " ");

        // 5. Stem each token.
            // Extract tokens from the page content into a list.
            List<String> tokens = Arrays.asList(parsedContent.split("[^a-z0-9]"));
            ArrayList<String> stems = new ArrayList<>();

            try {
                Stemmer stemmer = new Stemmer();
                for (String token : tokens) {
                    if(!token.equals("") && !stopWords.contains(token)) {
                        char[] word = token.toCharArray();
                        int wordLength = token.length();

//                    stemmer.add(word, wordLength);
                        for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
                        stemmer.stem();
                        stems.add(stemmer.toString());
                    }
                }
                //t2=System.currentTimeMillis();
                //System.out.println(t2-t1);

            } catch (Exception e) {
                e.printStackTrace();
            }


        return stems;
    }
}
