package com.indexer;

import com.crawler.PageContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InvertedIndex {
    private HashMap<Integer, List<Integer>> postings;
    private PageContent page = null;
    private final String stopWords = "A | a |The | the |An | an | of | but | at | therefore | be | can | also | they | is ";

    // Constructor of the main inverted index.
    public InvertedIndex(HashMap<Integer, List<Integer>> postings) {
        this.postings = postings;
    }

    // Constructor of the inverted index of a specific page.
    public InvertedIndex(PageContent page) {
        this.page = page;
    }

    // Returns a list of terms, which are going to be the keys in the invertedIndex.
    public ArrayList<String> parseCollection() {
        String parsedContent = "";

        // 1. Concatenate the title and the text of the page.
        parsedContent = page.getTitle() + page.getContent();

        // 2. Lowercase all words.
        parsedContent = parsedContent.toLowerCase();

        // 3. Get all alphanumeric tokens.
        parsedContent = parsedContent.replaceAll("[^a-zA-Z0-9_ ]", " ");

        // 4. Filter out stop words.
        parsedContent = parsedContent.replaceAll(stopWords , "");

        // 5. Stem each token.
            // Extract tokens from the page content into a list.
            List<String> tokens = Arrays.asList(parsedContent.split("[^a-z]"));
            ArrayList<String> stems = new ArrayList<>();

            try {
            Stemmer stemmer = new Stemmer();
                for (String token : tokens) {
                    char[] word = token.toCharArray();
                    int wordLength = token.length();

//                    stemmer.add(word, wordLength);
                    for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
                    stemmer.stem();
                    stems.add(stemmer.toString());
                    System.out.println("Hey " +  stemmer.toString() + " " + stems.size());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        return stems;
    }
}
