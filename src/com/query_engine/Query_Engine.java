
package com.query_engine;

import com.DbAdapter;
import com.indexer.Stemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mysql.jdbc.util.ResultSetUtil;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;


public class Query_Engine {
    private final DbAdapter db;
    private List<String> stopWords = Collections.emptyList();

    public Query_Engine(DbAdapter db){
        this.db = db;
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private ArrayList<String> stemQuery(String query){
        ArrayList<String> searchTerms = new ArrayList<String>();
        query = query.toLowerCase();
        List<String> tokens = Arrays.asList(query.split("[^a-z0-9]"));
        //  System.out.println(tokens);
        try {
            Stemmer stemmer = new Stemmer();
            for (String token : tokens) {
                if (!token.equals("") && !stopWords.contains(token)) {
                    char[] word = token.toCharArray();
                    int wordLength = token.length();
                    for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
                    stemmer.stem();
                    //  System.out.println(stemmer.toString());
                    searchTerms.add(stemmer.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchTerms;
    }
    private ResultSet search(String query, boolean images) throws SQLException {
        Integer [] page_ids = dbSearch(query,images);
        ResultSet resultSet = null;
        // RETRIEVING URLS, TITLE, BODY FROM IDS
        if(page_ids.length!=0) {
            if (images) {
                resultSet = this.db.getImageSRCs(page_ids);
            } else {
                resultSet = this.db.getPagesInfo(page_ids);
            }
        }
        else{
            System.out.println("No Results Found!");
        }
        return resultSet;
    }

    private ResultSet phraseSearch(String query)
    {
        ResultSet resultSet=null;
//
//        // STEMMING THE QUERY
//        ArrayList<String> searchTerms = stemQuery(query);
//        System.out.println(searchTerms);
//
//        //  GET PAGES WHERE TERMS MATCH
//        for (String term : searchTerms) {
//
//        }
        return resultSet;
    }

    private Integer [] dbSearch(String query,boolean images) throws SQLException {

        // STEMMING THE QUERY
        ArrayList<String> searchTerms = stemQuery(query);
        System.out.println(searchTerms);

        //****************************************************

        //  GETTING PAGES THAT ARE COMMON IN ALL TERMS
        ArrayList <Integer> pageIDS = findCommonPagesIDS(searchTerms, images);
        //  @todo ADD PAGES FEEHA BA2ET EL TERM
        System.out.println(pageIDS);

        //****************************************************

        // CALCULATING PAGES SCORES
        double [] pageScore = calculatePageScore(pageIDS,searchTerms);

        //****************************************************

        // SORTING PAGES ACCORDING TO SCORES
        Integer [] page_ids = pageIDS.toArray(new Integer[0]);
        sort(pageScore,page_ids);


        int pagesNumber = pageIDS.size();
        System.out.println("Pages IDS Sorted:");
        for(int i=0; i<pagesNumber;i++)
        {
            //System.out.print(pageScore[i]+ " ");
            System.out.print(page_ids[i]+" ");
        }


        return page_ids;
    }

    public ResultSet processQuery(String query,String country, boolean images)
    {
        ResultSet rs = null;
        if(images)
        {
            System.out.println("Images Search");
            try {
                addTrend(query, country);
                rs = search(query,true);
            } catch (SQLException e) {
                e.getErrorCode();
            }
        }
        else {
            if (query.startsWith("'") && query.endsWith("'")) {
                System.out.println("Phrase Search");
                rs = phraseSearch(query);
            } else {
                System.out.println("Normal Search");
                try {
                    addTrend(query, country);
                    rs = search(query, false);
                } catch (SQLException e) {
                    e.getErrorCode();
                }
            }
        }
        return rs;
    }
    private double [] calculatePageScore(ArrayList <Integer> pageIDS ,ArrayList<String> searchTerms)
    {
        int termsNumber = searchTerms.size();
        int pagesNumber = pageIDS.size();
        double [] pageScore = new double[pagesNumber];

        //ArrayList<ArrayList<Double>> PageColumn = new ArrayList<>();
        for (int i = 0; i < termsNumber; i++) {
            ArrayList<Double>TermRow = new ArrayList<>();
            for (int j = 0; j < pagesNumber; j++) {
                Double IDF = this.db.getIDF(searchTerms.get(i));
                Double TF = this.db.getTF(searchTerms.get(i), pageIDS.get(j));
                Double pageRank = this.db.getPR(pageIDS.get(j));
                TermRow.add(IDF*TF*pageRank);
                pageScore[j]+=IDF*TF*pageRank;
            }
            System.out.println(TermRow);
        }
        return pageScore;
    }

    private ArrayList <Integer> findCommonPagesIDS(ArrayList<String> searchTerms, boolean images)
    {
        ResultSet pageIdsResultSets = null;
        if(images)
        {
            pageIdsResultSets = this.db.selectCommonPages_images(searchTerms);
        }
        else{
            pageIdsResultSets = this.db.selectCommonPages(searchTerms);
        }
        ArrayList <Integer> pageIDS = new ArrayList<Integer>();
        try {
            while (pageIdsResultSets.next()) {
                pageIDS.add(pageIdsResultSets.getInt(1));
            }
        }
        catch( SQLException e){
            System.out.println(e.getErrorCode());
        }
        return pageIDS;
    }

    private void sort(double arr[],Integer[] pageIDS)
    {
        int n = arr.length;

        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (arr[j] > arr[min_idx])
                    min_idx = j;

            // Swap the found minimum element with the first
            // element
            double temp = arr[min_idx];
            Integer tempID = pageIDS[min_idx];
            arr[min_idx] = arr[i];
            pageIDS[min_idx] = pageIDS[i];
            arr[i] = temp;
            pageIDS[i] = tempID;
        }
    }

    public void addTrend(String query,String country){
        try{
            String serializedClassifier = "src/com/query_engine/english.all.3class.distsim.crf.ser.gz";
            AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
            List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(query);
            //Now I have a list of tiples each triple contains
            //  1. the type of entity -> PERSON/ORGANIZATION/LOCATION     we only need the person here
            //  2. the start index of this entity
            //  3. the end index of this entity
            String personName = "";
            for (Triple<String,Integer,Integer> entity : triples) {
                if(entity.first.equals("PERSON")) {
                    personName = query.substring(entity.second, entity.third);
                    // Add name and country to DB
                    this.db.addNameTrend(personName,country);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        String country="Egypt";
        qe.processQuery("manchester city and liverpool",country,true);
    }
}
