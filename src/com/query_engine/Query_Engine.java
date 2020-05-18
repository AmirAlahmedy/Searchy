
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

public class Query_Engine {
    private final DbAdapter db;
    private List<String> stopWords = Collections.emptyList();
    private ResultSet terms;
    private ResultSet ranks;

    public Query_Engine(DbAdapter db){
        this.db = db;
        this.terms = this.db.getTerms();
        this.ranks = this.db.getRanks();
        try{
            stopWords= Files.readAllLines(Paths.get("src/com/indexer/stopWords.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private ArrayList<String> stemQuery(String query){
        ArrayList<String> searchTerms = new ArrayList<String>();
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

    private ResultSet search(String query)
    {
        ResultSet resultSet=null;

        // STEMMING THE QUERY
        ArrayList<String> searchTerms = stemQuery(query);
        System.out.println(searchTerms);

        ResultSet pageIdsResultSets = this.db.selectCommonPages(searchTerms);
        ArrayList <Integer> pageIDS = new ArrayList<Integer>();
        try {
            while (pageIdsResultSets.next()) {
                pageIDS.add(pageIdsResultSets.getInt(1));
            }
        }
        catch( SQLException e){
            System.out.println(e.getErrorCode());
        }

        System.out.println(pageIDS);
        int termsNumber = searchTerms.size();
        int pagesNumber = pageIDS.size();
        //double [] termsIDF = new double[termsNumber];
        double [] pageScore = new double[pagesNumber];

        //ArrayList<ArrayList<Double>> PageColumn = new ArrayList<>();
        for (int i = 0; i < termsNumber; i++) {
            ArrayList<Double>TermRow = new ArrayList<>();
            for (int j = 0; j < pagesNumber; j++) {
                //termsIDF[i] = this.db.getIDF(searchTerms.get(i));
                Double IDF = this.db.getIDF(searchTerms.get(i));
                Double TF = this.db.getTF(searchTerms.get(i), pageIDS.get(j));
                Double pageRank = this.db.getPR(pageIDS.get(j));
                TermRow.add(IDF*TF*pageRank);
                pageScore[j]+=IDF*TF*pageRank;
            }
            System.out.println(TermRow);
            //PageColumn.add(TermRow);
        }
        for(int i=0; i<pagesNumber;i++)
        {
            System.out.print(pageScore[i]+" ");
        }
        return resultSet;
    }

    public ResultSet processQuery(String query)
    {
        if(query.startsWith("'") &&  query.endsWith("'")) {
            System.out.println("Phrase Search");
            return phraseSearch(query);
        }
        else {
            System.out.println("Normal Search");
            return search(query);
        }
    }
    public static void main(String[] args) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        qe.processQuery("arsenal and manchester and liverpool ");
    }
}
