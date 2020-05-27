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

public class InvertedIndex implements Runnable{
    //private ResultSet resultSet;
    private List<String> stopWords = Collections.emptyList();
    private DbAdapter dbAdapter;
    private int pagesCount;
    private int noThreads;

    // Constructor of the inverted index of a specific page.
    public InvertedIndex(int noThreads) {
        this.dbAdapter =  new DbAdapter();
        //this.resultSet = this.dbAdapter.readPages();
        this.pagesCount = this.dbAdapter.pagesRows();
        this.noThreads=noThreads;
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Returns a list of terms, which are going to be the keys in the invertedIndex.
    public void parseCollection(ResultSet myResultSet) throws SQLException {

        Stemmer stemmer = new Stemmer();
        while (myResultSet.next()) {
            if(myResultSet.getBoolean("indexed"))
                continue;

            int wordsInPage = 0;
            int pageId = myResultSet.getInt("id");
            //int words = resultSet.getInt("words");
            String parsedContent;
            //Delete old index if found
            dbAdapter.deleteOldIndex(pageId);

            // Depends on the order of the columns in the pages table.
            for (int i = 3; i <= 11; ++i) {
                parsedContent = myResultSet.getString(i);

                // 1. Lowercase all words.
                parsedContent = parsedContent.toLowerCase();

                if (i == 11) {         // to extract the the image alt and src only
                    String [] srcs = myResultSet.getString(12).split("\n\n");
                    String[] alts = parsedContent.split("\n\n");
                    //System.out.println(Integer.toString(srcs.length) + "    " +Integer.toString(alts.length));
                    int counter = 0;
                    for (String img : alts){
                        if(!img.equals("")) {
//                            if(!srcs[counter].startsWith("http")){
//                                counter = counter + 1;
//                                continue;
//                            }
                            // the first element is the alt and the second is the src
                            //img = img.replaceAll("[^a-z0-9]", " ");
                            List<String> tokens = new ArrayList<String>(Arrays.asList(img.split("[^a-z0-9]")));
                            tokens.removeAll(stopWords);
                            try {
                                //Stemmer stemmer = new Stemmer();
                                for (String token : tokens) {
                                    if (!token.equals("") ){ //&& !stopWords.contains(token)) {
//                                        char[] word = token.toCharArray();
//                                        int wordLength = token.length();
//                                        for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
//                                        stemmer.stem();
                                        dbAdapter.addNewImg(pageId,stemmer.stem(token),srcs[counter]);

                                    }
                                }
                                counter = counter +1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                } else {

                    // 2. Get all alphanumeric tokens.
                    //parsedContent = parsedContent.replaceAll("[^a-z0-9]", " ");



                    // 3. Filter out stop words and stem each token.
                    // Extract tokens from the page content into a list.

                    List<String> tokens = new ArrayList<String>(Arrays.asList(parsedContent.split("[^a-z0-9]")));
                    tokens.removeAll(stopWords);
                    try {
                        //Stemmer stemmer = new Stemmer();
                        for (String token : tokens) {
                            if (!token.equals("") ){//&& !stopWords.contains(token)) {
                                wordsInPage++;
//                                char[] word = token.toCharArray();
//                                int wordLength = token.length();
//                                for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
//                                stemmer.stem();
                                dbAdapter.addNewTerm(stemmer.stem(token), pageId, i);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            // End of one document
            //Need to set page word count & update the right TF by dividing on the total words
            //dbAdapter.updatePageWordCount(pageId,wordsInPage);
            //dbAdapter.updateTF(pageId,wordsInPage);
            //dbAdapter.markPageAsIndexed(pageId);
        }
        //End of all documents
        //Need to set idf
        //dbAdapter.setIDF();
    }
    @Override

    public void run() {
        int threadNumber = Integer.parseInt(Thread.currentThread().getName());
        int pagesPerThread = pagesCount / noThreads;
        //System.out.println(noThreads);
        System.out.println(threadNumber);
        ResultSet myResultSet;
        for (int i = 0; i < noThreads; i++) {
            if (threadNumber == i) {
                if(threadNumber == noThreads-1){
                    int remainingPages = pagesCount - (noThreads-1)*pagesPerThread;
                    myResultSet = this.dbAdapter.readPagesThreads(remainingPages,i*pagesPerThread);
                } else {
                    myResultSet = this.dbAdapter.readPagesThreads(pagesPerThread,i*pagesPerThread);
                }
                //System.out.println(myPivots.get(0).getPivot());
                try {
                    parseCollection(myResultSet);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        }
    }
    public void setIDFAllTerms(){
        dbAdapter.setIDF();
    }

    public static void main(String[] args) throws InterruptedException {

//        CopyOnWriteArrayList<Pivot> pivots = new CopyOnWriteArrayList<>();
//        pivots.add(new Pivot("https://en.wikipedia.org/wiki/Augmented_reality"));
//
//        Crawler crawler = new Crawler(pivots, 1);
//        crawler.crawl();
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of threads: ");
        int number = input.nextInt();
        input.close();
//
//
        Runnable indexer = new InvertedIndex(number);
        ArrayList<Thread> threadArr=new ArrayList<>();
        for(int i=0;i<number;i++){
            threadArr.add(new Thread(indexer));
            threadArr.get(i).setName(Integer.toString(i));
        }
        long start=System.currentTimeMillis();
        for(int i=0;i<number;i++){
            threadArr.get(i).start();
        }

        for(int i=0;i<number;i++){
            threadArr.get(i).join();
        }
        long finish=System.currentTimeMillis();
        System.out.println(finish-start);
        System.out.println("Started IDF Setting");
        ((InvertedIndex) indexer).setIDFAllTerms();

    }

}
