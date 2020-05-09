package com.ranker;

import com.DbAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class PageRank {
    private WebGraph graph;
    private DbAdapter db;
    private ResultSet resultSet;
    private int N;  //total number of web pages
    private int d;  //damping ratio

    public PageRank() throws IOException, SQLException {
        this.graph = new WebGraph(this.N);
        this.db = new DbAdapter();
        this.resultSet = this.db.readURLID();

        // Build the web graph.
        buildWebGraph();
        // Display the graph.
        this.graph.printGraph();
    }

    public void makePageRanks() {
        //1 Initially, each web page will have a rank of 1/N

        //2 Update

        //3 Convergence check
    }

    private void buildWebGraph() throws SQLException, IOException {
        while (this.resultSet.next()) {
            int from = resultSet.getInt("id");
            String url = resultSet.getString("url");
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.body().select("a[href]");
            for (Element link : links) {
                String childURL = link.attr("href");
                ResultSet resultSet1 = this.db.readID(childURL);

                if(resultSet1.next()) {
                    int to = resultSet1.getInt("id");
                    this.graph.addDirectedEdge(from, to);
                }
            }
        }
    }


    static public class WebGraph {
        private ArrayList<ArrayList<Integer>> adj;
        private DbAdapter db;

         public WebGraph(int nodes){
             //TODO: Get the length of the list from the database.
             this.db = new DbAdapter();
             nodes = this.db.pagesRows() + 1;    // Number of rows in the table pages, i.e. number of crawled pages.
             this.adj = new ArrayList<ArrayList<Integer>>(nodes);
             for (int i = 0; i < nodes; i++)
                 adj.add(new ArrayList<Integer>());

         }

         public void addDirectedEdge(int from, int to) {
            adj.get(from).add(to);
         }

        public void printGraph()
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

    public static void main(String[] args) throws IOException, SQLException {
        PageRank pageRank = new PageRank();
    }
}
