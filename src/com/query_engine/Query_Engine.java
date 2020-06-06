
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<String> removeStopWords(String query){
        ArrayList<String> searchTerms = new ArrayList<String>();
        query = query.toLowerCase();
        List<String> tokens = Arrays.asList(query.split("[^a-z0-9]"));
        try {
            for (String token : tokens) {
                if (!token.equals("") && !stopWords.contains(token)) {
                    char[] word = token.toCharArray();
                    int wordLength = token.length();
                    String term="";
                    for (int c = 0; c < wordLength; c++) term+=word[c];
                    searchTerms.add(term);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchTerms;
    }

    private ResultSet search(String query, boolean images, ArrayList<String> snippets) throws SQLException {
        ArrayList<String> searchTerms = stemQuery(query);
        ArrayList<String> termsNonStemmed = removeStopWords(query);
//        snippets = removeStopWords(query);

        Integer [] commonPages = dbSearch(query,images,true);
        ArrayList<Integer> allPagesIDS = new ArrayList<>();
        for (Integer page_id : commonPages) {
            allPagesIDS.add(page_id);
        }
        //System.out.println("Common images bas:"+allPagesIDS);
        Integer [] restOfThePages = dbSearch(query,images,false);
        for (Integer page_id : restOfThePages) {
            if (!allPagesIDS.contains(page_id)) {
                allPagesIDS.add(page_id);
            }
        }
        Integer [] page_ids = allPagesIDS.toArray(new Integer[0]);

        if(page_ids.length!=0) {
            if (images) {
                return this.db.getImagesInfo(page_ids, searchTerms);
            } else {
                ResultSet resultSet = this.db.getPagesInfo(page_ids);
                try {
                    getSnippets(snippets, termsNonStemmed, resultSet);
                }catch (SQLException e)
                {
                    e.getErrorCode();
                }
                resultSet.beforeFirst();
                return resultSet;
            }
        }
        else{
            //System.out.println("No Results Found!");
        }
        return null;
    }

    private ResultSet phraseSearch(String query, ArrayList<String> snippets) throws SQLException {

        ArrayList<String> termsNonStemmed = removeStopWords(query);
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

        Integer [] finalIDS = matched_ids.toArray(new Integer[0]);
        resultSet = this.db.getPagesInfo(finalIDS);
        try {
            getSnippets(snippets, termsNonStemmed, resultSet);
            resultSet.beforeFirst();
        }catch (SQLException e)
        {
            e.getErrorCode();
        }
        return resultSet;
    }

    private void getSnippets(ArrayList<String> snippets, ArrayList<String> termsNonStemmed, ResultSet resultSet) throws SQLException {
        while(resultSet.next()) {
            String body = resultSet.getString(4);
            StringBuilder snippet = new StringBuilder();
            for (String term : termsNonStemmed) {
                ArrayList<Integer> startIndices = findMatches(body.toLowerCase(), term.toLowerCase());
                if (startIndices.size() != 0) {
                    for (int i = 0; i < Math.min(startIndices.size(), 4); i++) {
                        int endIndex = body.indexOf(" ", startIndices.get(i) + 25) == -1 ?
                                body.length() - 1 : body.indexOf(" ", startIndices.get(i) + 25);
                        snippet.append(body.substring(startIndices.get(i), endIndex)).append("...");
                    }
                }
            }
            snippets.add(snippet.toString());
        }
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
        return doesItMatch(pageText,query);
        //return KMPSearch(query, pageText);
    }

//

    private Integer [] dbSearch(String query,boolean images, boolean common) throws SQLException {

        // STEMMING THE QUERY
        ArrayList<String> searchTerms = stemQuery(query);
        //System.out.println(searchTerms);

        //  GETTING PAGES THAT ARE COMMON IN ALL TERMS
        ArrayList <Integer> pageIDS = findCommonPagesIDS(searchTerms, images, common);
        //System.out.println(pageIDS);

        // CALCULATING PAGES SCORES
        double [] pageScore = calculatePageScore(pageIDS,searchTerms);

        // SORTING PAGES ACCORDING TO SCORES
        Integer [] page_ids = pageIDS.toArray(new Integer[0]);
        //sort(pageScore,page_ids);
        quickSort(pageScore,page_ids,0,pageScore.length-1);

        //int pagesNumber = pageIDS.size();

        return page_ids;
    }

    public ResultSet processQuery(String query,String country, boolean images, ArrayList<String> snippets)
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
                rs = search(query,true, snippets);
            } catch (SQLException e) {
                e.getErrorCode();
            }
        }
        else {
            if (query.startsWith("'") && query.endsWith("'")) {
                System.out.println("Phrase Search");
                try {
                    rs = phraseSearch(query, snippets);
                }
                catch (SQLException e )
                {
                    e.getErrorCode();
                }
            } else {
                System.out.println("Normal Search");
                try {
                    rs = search(query, false, snippets);
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
                // System.out.println("IDF set to :" + IDF);
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
            //System.out.println(TermRow);
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
            //System.out.println(e.getErrorCode());
        }
        return pageIDS;
    }

    ///////////

    int partition(double arr[],Integer[] pageIDS, int low, int high)
    {
        double pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            //if (arr[j] < pivot)
            if(arr[j] > pivot)
            {
                i++;

                // swap arr[i] and arr[j]
                double temp = arr[i];
                int temp2= pageIDS[i];

                arr[i] = arr[j];
                pageIDS[i] = pageIDS[j];

                arr[j] = temp;
                pageIDS[j] = temp2;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        double temp = arr[i+1];
        int temp2= pageIDS[i+1];

        arr[i+1] = arr[high];
        pageIDS[i+1] = pageIDS[high];

        arr[high] = temp;
        pageIDS[high] = temp2;
        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    void quickSort(double arr[],Integer[] pageIDS, int low, int high)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr,pageIDS, low, high);

            // Recursively sort elements before
            // partition and after partition
            quickSort(arr, pageIDS, low, pi-1);
            quickSort(arr, pageIDS,pi+1, high);
        }
    }
    ///////////
//    private void sort(double arr[],Integer[] pageIDS)
//    {
//        int n = arr.length;
//
//        // One by one move boundary of unsorted subarray
//        for (int i = 0; i < n-1; i++)
//        {
//            // Find the minimum element in unsorted array
//            int min_idx = i;
//            for (int j = i+1; j < n; j++)
//                if (arr[j] > arr[min_idx])
//                    min_idx = j;
//
//            // Swap the found minimum element with the first
//            // element
//            double temp = arr[min_idx];
//            Integer tempID = pageIDS[min_idx];
//            arr[min_idx] = arr[i];
//            pageIDS[min_idx] = pageIDS[i];
//            arr[i] = temp;
//            pageIDS[i] = tempID;
//        }
//    }

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

    private ArrayList<Integer> findMatches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        ArrayList<Integer> startIndices = new ArrayList<>();
        // Check all occurrences
        while (matcher.find()) {
            startIndices.add(matcher.start());
//            System.out.print("Start index: " + matcher.start());
//            System.out.print(" End index: " + matcher.end());
//            System.out.println(" Found: " + matcher.group());
        }
        return startIndices;
    }
    private boolean doesItMatch(String text, String phrase) {
        Pattern pattern = Pattern.compile(phrase);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        String country="Egypt";
        ArrayList<String> snippets = new ArrayList<>();
        long now = System.currentTimeMillis();
        System.out.println(now);
        qe.processQuery("Premier League",country,false, snippets);
        long after = System.currentTimeMillis();
        System.out.println(after-now);


    }
}
//    private boolean KMPSearch(String pat, String txt)
//    {
//        boolean found = false;
//
//        int M = pat.length();
//        int N = txt.length();
//
//        // create lps[] that will hold the longest
//        // prefix suffix values for pattern
//        int lps[] = new int[M];
//        int j = 0; // index for pat[]
//
//        // Preprocess the pattern (calculate lps[]
//        // array)
//        computeLPSArray(pat, M, lps);
//
//        int i = 0; // index for txt[]
//        while (i < N) {
//            if (pat.charAt(j) == txt.charAt(i)) {
//                j++;
//                i++;
//            }
//            if (j == M) {
//                found = true;
//                j = lps[j - 1];
//                //return found;
//            }
//
//            // mismatch after j matches
//            else if (i < N && pat.charAt(j) != txt.charAt(i)) {
//                // Do not match lps[0..lps[j-1]] characters,
//                // they will match anyway
//                if (j != 0)
//                    j = lps[j - 1];
//                else
//                    i = i + 1;
//            }
//        }
//        return found;
//    }
//
//    private void computeLPSArray(String pat, int M, int lps[])
//    {
//        // length of the previous longest prefix suffix
//        int len = 0;
//        int i = 1;
//        lps[0] = 0; // lps[0] is always 0
//
//        // the loop calculates lps[i] for i = 1 to M-1
//        while (i < M) {
//            if (pat.charAt(i) == pat.charAt(len)) {
//                len++;
//                lps[i] = len;
//                i++;
//            }
//            else // (pat[i] != pat[len])
//            {
//                // This is tricky. Consider the example.
//                // AAACAAAA and i = 7. The idea is similar
//                // to search step.
//                if (len != 0) {
//                    len = lps[len - 1];
//
//                    // Also, note that we do not increment
//                    // i here
//                }
//                else // if (len == 0)
//                {
//                    lps[i] = len;
//                    i++;
//                }
//            }
//        }
//    }