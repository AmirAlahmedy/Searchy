package com.ranker;

import com.DbAdapter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import com.crawler.Crawler;
import com.crawler.PageContent;
import com.crawler.Pivot;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class PageRank {
    private WebGraph graph;
    private DbAdapter db;
    private ResultSet resultSet;

    public PageRank() throws IOException, SQLException {
        this.graph = new WebGraph();
        this.db = new DbAdapter();
        this.resultSet = this.db.readURLID();

        // Build the web graph.
        buildWebGraph();
        // Display the graph.
        this.graph.printGraph();
    }

    private void buildWebGraph() throws SQLException, IOException {
        while (this.resultSet.next()) {
            int from = resultSet.getInt("id");
            String url = resultSet.getString("url");
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.body().select("a[href]");
            for (Element link : links) {
                String childURL = link.attr("href");
                //TODO: If exists in the database get its id.
                ResultSet resultSet1 = this.db.readID(childURL);

                if(resultSet1.next()) {
                    int to = resultSet1.getInt("id");
                    this.graph.addDirectedEdge(from, to);
                }
            }
        }
    }


    static public class WebGraph {
        ArrayList<ArrayList<Integer>> adj;

         public WebGraph() {
             this.adj = new ArrayList<ArrayList<Integer>>(50);
             for (int i = 0; i < 50; i++)
                 adj.add(new ArrayList<Integer>());
         }

         void addDirectedEdge(int from, int to) {
            adj.get(from).add(to);
         }

        void printGraph()
        {
            for (int i = 0; i < this.adj.size(); i++) {
                System.out.println("\nAdjacency list of vertex" + i);
                for (int j = 0; j < this.adj.get(i).size(); j++) {
                    System.out.print(" -> "+this.adj.get(i).get(j));
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, SQLException {
        PageRank pageRank = new PageRank();
    }
}
