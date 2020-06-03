
package com.query_engine;

import com.DbAdapter;
import com.indexer.Stemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mysql.jdbc.util.ResultSetUtil;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;


public class Query_Engine {
    private final DbAdapter db;
    private List<String> stopWords = Collections.emptyList();
    private final int alldocs;
    private int todayDate;
    private String country;

    public Query_Engine(DbAdapter db){
        this.db = db;
        this.alldocs = this.db.pagesRows();
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
                    //searchTerms.add(stemmer.toString().toLowerCase());
                    searchTerms.add(stemmer.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchTerms;
    }
    private ResultSet search(String query, boolean images) throws SQLException {
        ArrayList<String> searchTerms = stemQuery(query);

        Integer [] commonPages = dbSearch(query,images,true);
        ArrayList<Integer> allPagesIDS = new ArrayList<>();
        for (Integer page_id : commonPages) {
            allPagesIDS.add(page_id);
        }
        System.out.println("Common images bas:"+allPagesIDS);
        Integer [] restOfThePages = dbSearch(query,images,false);
        for (Integer page_id : restOfThePages) {
            if (!allPagesIDS.contains(page_id)) {
                allPagesIDS.add(page_id);
            }
        }
        Integer [] page_ids = allPagesIDS.toArray(new Integer[0]);
        ResultSet resultSet = null;
        if(page_ids.length!=0) {
            if (images) {
                resultSet= this.db.getImagesInfo(page_ids, searchTerms);
//                    while (resultSet.next()) {
//                        System.out.println("Image src: "+resultSet.getString(1));
//                        System.out.println("Image url: "+resultSet.getString(2));
//                    }
            } else {
                resultSet = this.db.getPagesInfo(page_ids);
            }
        }
        else{
            System.out.println("No Results Found!");
        }
        return resultSet;
    }

    private ResultSet phraseSearch(String query) throws SQLException {

        ResultSet resultSet=null;
        Integer [] page_ids = dbSearch(query,false,true);
        ArrayList<Integer> matched_ids = new ArrayList<>();
        for (Integer id : page_ids)
        {
            if(containsPhrase(query,id))
            {
                matched_ids.add(id);
            }
        }
        System.out.println("\nPhrase Matched IDS ONLY:");
        System.out.println(matched_ids);
        for (Integer page_id : page_ids) {
            if (!matched_ids.contains(page_id)) {
                matched_ids.add(page_id);
            }
        }
        Integer [] restOfTheMatches = dbSearch(query,false,false);
        for (Integer page_id : restOfTheMatches) {
            if (!matched_ids.contains(page_id)) {
                matched_ids.add(page_id);
            }
        }
        System.out.println("\nAll Matched IDS Sorted phrase first:");
        System.out.println(matched_ids);
        Integer [] finalIDS = matched_ids.toArray(new Integer[0]);
        resultSet = this.db.getPagesInfo(finalIDS);

        return resultSet;
    }

    private boolean containsPhrase(String query, Integer page_id) throws SQLException {
        // GET TEXT
        ResultSet rs = this.db.getPageRow(page_id);
        String pageText = rs.getString(1) + rs.getString(2) + rs.getString(3)
                + rs.getString(4) + rs.getString(5) + rs.getString(6)
                + rs.getString(7) +rs.getString(8);

        query = query.substring(1, query.length()-1);
        query = query.toLowerCase();
        pageText = pageText.toLowerCase();
        // APPLY KMP
        return KMPSearch(query, pageText);
    }

    private boolean KMPSearch(String pat, String txt)
    {
        boolean found = false;

        int M = pat.length();
        int N = txt.length();

        // create lps[] that will hold the longest
        // prefix suffix values for pattern
        int lps[] = new int[M];
        int j = 0; // index for pat[]

        // Preprocess the pattern (calculate lps[]
        // array)
        computeLPSArray(pat, M, lps);

        int i = 0; // index for txt[]
        while (i < N) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                found = true;
                j = lps[j - 1];
                //return found;
            }

            // mismatch after j matches
            else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return found;
    }

    private void computeLPSArray(String pat, int M, int lps[])
    {
        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;
        lps[0] = 0; // lps[0] is always 0

        // the loop calculates lps[i] for i = 1 to M-1
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            }
            else // (pat[i] != pat[len])
            {
                // This is tricky. Consider the example.
                // AAACAAAA and i = 7. The idea is similar
                // to search step.
                if (len != 0) {
                    len = lps[len - 1];

                    // Also, note that we do not increment
                    // i here
                }
                else // if (len == 0)
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }

    private Integer [] dbSearch(String query,boolean images, boolean common) throws SQLException {

        // STEMMING THE QUERY
        ArrayList<String> searchTerms = stemQuery(query);
        System.out.println(searchTerms);

        //  GETTING PAGES THAT ARE COMMON IN ALL TERMS
        ArrayList <Integer> pageIDS = findCommonPagesIDS(searchTerms, images, common);
        System.out.println(pageIDS);

        // CALCULATING PAGES SCORES
        double [] pageScore = calculatePageScore(pageIDS,searchTerms);

        // SORTING PAGES ACCORDING TO SCORES
        Integer [] page_ids = pageIDS.toArray(new Integer[0]);
        sort(pageScore,page_ids);


        int pagesNumber = pageIDS.size();
        System.out.println("Pages IDS Sorted:");
        for(int i=0; i<pagesNumber;i++)
        {
            System.out.print(page_ids[i]+" ");
            //System.out.print(pageScore[i]+" ");
        }
        System.out.println();

        return page_ids;
    }

    public ResultSet processQuery(String query,String country, boolean images)
    {
        //Running trends in a different thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                addTrend(query,country);
            }
        }).start();

        //Adding Query to database for Auto-Complete in a different thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                addSuggestion(query);
            }
        }).start();

        ResultSet rs = null;
        //  Save date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());
        this.todayDate = Integer.parseInt(date);
        //  Save Country
        this.country = country;
        if(images)
        {
            System.out.println("Images Search");
            try {
                rs = search(query,true);
            } catch (SQLException e) {
                e.getErrorCode();
            }
        }
        else {
            if (query.startsWith("'") && query.endsWith("'")) {
                System.out.println("Phrase Search");
                try {
                    rs = phraseSearch(query);
                }
                catch (SQLException e )
                {
                    e.getErrorCode();
                }
            } else {
                System.out.println("Normal Search");
                try {
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
            Double IDF = this.db.getIDF(searchTerms.get(i));
            if(IDF==0.0D)
            {
                IDF=this.db.setTermIDF(searchTerms.get(i),this.alldocs);
                System.out.println("IDF set to :" + IDF);
            }
            for (int j = 0; j < pagesNumber; j++) {
                Double contextScore = 0.0D;
                //  DATE
                Integer pageDate =this.db.getPageDate(pageIDS.get(j));
                if(pageDate!=1) // FEEH DATE LEL PAGE
                {
                    if(pageDate==this.todayDate) {
                        contextScore+=1.0D;
                    }
                    else {
                        contextScore+=1/(this.todayDate-pageDate);
                    }
                }
                // TITLE,H1,H2,H3..ETC
                ResultSet context = this.db.getContextScore(searchTerms.get(i),pageIDS.get(j));
                if (context != null) {
                    try {
                        contextScore += context.getDouble(1)*2 + context.getDouble(2)*1.5
                                + context.getDouble(3)*0.3 + context.getDouble(4)*0.2
                                + context.getDouble(5)*0.1 + context.getDouble(6)*0.05
                                + context.getDouble(7)*0.05;


                    } catch (SQLException e) {
                        e.getErrorCode();
                    }
                }
                // COUNTRY
                String pageCountry = this.db.getPageCountry(pageIDS.get(j));
                if(pageCountry==this.country)
                {
                    contextScore+=10;
                }
                Double TF = this.db.getTF(searchTerms.get(i), pageIDS.get(j));
                if(TF!=0.0D) {
                    Double pageRank = this.db.getPR(pageIDS.get(j));
                    TermRow.add((IDF * TF * pageRank) + contextScore);
                    pageScore[j] += (IDF * TF * pageRank) + contextScore;
                }
            }
            System.out.println(TermRow);
        }
        return pageScore;
    }

    private ArrayList <Integer> findCommonPagesIDS(ArrayList<String> searchTerms, boolean images, boolean common)
    {
        ResultSet pageIdsResultSets = null;
        if(images)
        {
            pageIdsResultSets = this.db.selectImages(searchTerms,common);
        }
        else{
            pageIdsResultSets = this.db.selectPages(searchTerms,common);
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
    public void addSuggestion(String query){
        db.addSuggestion(query);
    }

    public static void main(String[] args) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        String country="Egypt";
        qe.processQuery("bayern munchen",country,false);
//        ResultSet rs =db.getTrends("Egypt");
//        while (rs.next()){
//            System.out.println(rs.getString(2));
//        }
    }
}
