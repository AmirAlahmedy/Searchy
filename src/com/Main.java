package com;

import com.crawler.Pivot;
import com.indexer.Stemmer;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import com.query_engine.Query_Engine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class Main {
    ///////////

    ///////////
    public static void main(String[] args) throws IOException {


//        double tita [] ={0.1,0.02,0.3,0.9,0.05,0.2,1.0,2.2,0.008,0.023,0.014,0.5,4.9};
//        Integer ids [] = {1,2,3,4,5,6,7,8,9,10,11,12,13};
//        quickSort(tita,ids,0,tita.length-1);
//        //sort(tita,ids);
//        int i=0;
//        for(double t : tita)
//        {
//            System.out.print(t+ " ");
//
//        }
//        System.out.println();
//        for(Integer id : ids) {
//            System.out.print(id + " ");
//        }
//        DbAdapter db = new DbAdapter();
//        Query_Engine qe = new Query_Engine(db);
//        String country="Egypt";
//        qe.processQuery("'The Premier League'",country,false);

    }


    private static void sort(double arr[],Integer[] pageIDS)
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
    static int partition(double arr[], Integer[] pageIDS, int low, int high)
    {
        double pivot = arr[high];
        //double pivot = arr[low];
        int i = (low-1); // index of smaller element
        //int i = high-1;
        for (int j=low; j<high; j++)
        //for(int j=high-1; j>=low; j--)
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
    private static void quickSort(double arr[],Integer[] pageIDS, int low, int high)
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
}
