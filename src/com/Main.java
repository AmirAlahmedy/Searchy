package com;

import com.indexer.Stemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
//    private boolean containsPhrase(String query, Integer page_id) throws SQLException {
//        // GET TEXT
//        ResultSet rs = this.db.getPageRow(page_id);
//        String pageText = rs.getString(1) + rs.getString(2) + rs.getString(3)
//                + rs.getString(4) + rs.getString(5) + rs.getString(6)
//                + rs.getString(7) +rs.getString(8);
//        System.out.println(pageText);
//        // APPLY KMP
//        return KMPSearch(query, pageText);
//    }

    static boolean KMPSearchh(String pat, String txt)
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
        computeLPSArrayy(pat, M, lps);

        int i = 0; // index for txt[]
        while (i < N) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                found = true;
                System.out.println("Found pattern "
                        + "at index " + (i - j));
                j = lps[j - 1];
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

    static void computeLPSArrayy(String pat, int M, int lps[])
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
    public static void main(String[] args) throws IOException {

        System.out.println(KMPSearchh("karim and ibrahim", "karim and ibrahim loloo"));

//        Document document= Jsoup.connect("https://cademartin.com/").get();
//        Elements img =document.select("img");
//        String alt="";
//        for (Element el : img){
//            if(el.attr("alt") != null && el.attr("alt") != ""){
//                alt= alt + el.attr("alt") +"\t"+el.attr("src")+ "\n";
//            }
//        }
//        System.out.println(alt);

//        Stemmer stemmer = new Stemmer();
//        String[] words={"hello", "killing", "biggest"};
//        for(String token : words){
//            System.out.println(stemmer.stem(token));
//            char[] word = token.toCharArray();
//            int wordLength = token.length();
//            for (int c = 0; c < wordLength; c++) stemmer.add(word[c]);
//            stemmer.stem();
//            System.out.println(stemmer.toString());
//        }

    }

}
