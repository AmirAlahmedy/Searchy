package com.indexer;

import com.DbAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InvertedIndex implements Runnable {
    //private ResultSet resultSet;
    private List<String> stopWords = Collections.emptyList();
    private DbAdapter dbAdapter;
    private int pagesCount;
    private int noThreads;

    // Constructor of the inverted index of a specific page.
    public InvertedIndex(int noThreads) {
        this.dbAdapter = new DbAdapter();
        this.pagesCount = this.dbAdapter.pagesRowsNotIndexed();
        this.noThreads=noThreads;
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns a list of terms, which are going to be the keys in the invertedIndex.
    public void parseCollection(ResultSet myResultSet) throws SQLException {

        Stemmer stemmer = new Stemmer();
        while (myResultSet.next()) {
            if (myResultSet.getBoolean("indexed"))
                continue;

            int wordsInPage = 0;
            int pageId = myResultSet.getInt("id");
            // FOR DEBUGGING printing when a thread starts a new page
            System.out.println(Thread.currentThread().getName() + "Started page No." + Integer.toString(pageId));


            String parsedContent;
            // Delete old index with the same page ID if found
            dbAdapter.deleteOldIndex(pageId);

            // Depends on the order of the columns in the pages table.
            for (int i = 3; i <= 11; ++i) {
                parsedContent = myResultSet.getString(i);

                // Lowercase all words.
                parsedContent = parsedContent.toLowerCase();

                if (i == 11) {         // To extract the the image alt and src only
                    String[] srcs = myResultSet.getString(12).split("\n\n");
                    String[] alts = parsedContent.split("\n\n");

                    int counter = 0;
                    for (String img : alts) {
                        if (!img.equals("")) {
                            // Splitting the text and then removing stop words
                            List<String> tokens = new ArrayList<>(Arrays.asList(img.split("[^a-z0-9]")));
                            tokens.removeAll(stopWords);
                            try {
                                for (String token : tokens) {
                                    if (!token.equals("")) {
                                        // Adding the stemmed token to the db
                                        dbAdapter.addNewImg(pageId, stemmer.stem(token), srcs[counter]);
                                    }
                                }
                                counter = counter + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                } else {

                    // Splitting the text and then removing stop words
                    List<String> tokens = new ArrayList<>(Arrays.asList(parsedContent.split("[^a-z0-9]")));
                    tokens.removeAll(stopWords);
                    try {
                        for (String token : tokens) {
                            if (!token.equals("")) {
                                // Increasing the words count in the page and adding the stemmed token to the db
                                wordsInPage++;
                                dbAdapter.addNewTerm(stemmer.stem(token), pageId, i);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            // End of one document
            // Need to set page word count & update the right TF by dividing on the total words
            // Then marking the page as indexed
            dbAdapter.updateTF(pageId, wordsInPage);
            dbAdapter.markPageAsIndexed(pageId);
            //FOR DEBUGGING printing the page ID that finished
            System.out.println(pageId);
        }
    }

    @Override

    public void run() {
        int threadNumber = Integer.parseInt(Thread.currentThread().getName());
        int pagesPerThread = pagesCount / noThreads;
        // FOR DEBUGGING printing the thread number to make sure it started
        System.out.println(threadNumber);
        ResultSet myResultSet;

        //Splitting the table on the threads using limit and offset in mysql
        if(threadNumber == noThreads-1){
            //If it is the last thread it needs to take the remaining pages
            int remainingPages = pagesCount - (noThreads-1)*pagesPerThread;
            myResultSet = this.dbAdapter.readPagesThreads(remainingPages,threadNumber*pagesPerThread);
        } else {
            myResultSet = this.dbAdapter.readPagesThreads(pagesPerThread,threadNumber*pagesPerThread);
        }
        try {
            //Each thread calling parse collection with its result set
            parseCollection(myResultSet);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void setIDFAllTerms() {
        dbAdapter.setIDF();
    }

    public static void main(String[] args) throws InterruptedException {


        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of threads: ");
        int number = input.nextInt();
        input.close();

        Runnable indexer = new InvertedIndex(number);
        ArrayList<Thread> threadArr = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            threadArr.add(new Thread(indexer));
            threadArr.get(i).setName(Integer.toString(i));
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            threadArr.get(i).start();
        }

        for (int i = 0; i < number; i++) {
            threadArr.get(i).join();
        }
        long finish = System.currentTimeMillis();
        System.out.println(finish - start);

    }

}
