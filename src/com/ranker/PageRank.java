package com.ranker;

import com.DbAdapter;
import com.crawler.Pivot;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class PageRank {
    private final WebGraph graph;
    private final DbAdapter db;
    private final ResultSet resultSet; //a ResultSet pointing to the pages table
    private final int N;  //total number of web pages
    /**
     * @implNote d is the damping ratio/decay factor, 85% of your users will be willing to continue visiting new web pages.
     * The other 15% simply stop where they are.
     */
    private final double d = .85;
    private final double epsilon = .1;
    private final int iterations = 100;

    public PageRank() {
        this.db = new DbAdapter();
        this.N = this.db.pagesRows();
        this.graph = new WebGraph(this.N);
        this.resultSet = this.db.readURLID();
    }

    public void makePageRanks() {
        //1 Initially, each web page will have a rank of 1/N
        this.db.fillRanks((double) 1 / N);
        boolean isConverged;
        int iter = 0;
        //2 Update
        do {
            iter++;
            isConverged = true;
            for (int i = 1; i <= N; ++i) {
                if (!graph.adj.get(i).isEmpty()) {
                    for (Integer adjacentPage : graph.adj.get(i)) {
                        isConverged = isConverged && updatePR(i, adjacentPage, graph.outdegree(i)) <= epsilon;
                    }
                } else {
                    isConverged = isConverged && updatePR(i, -1, N) <= epsilon;
                }
            }
            //3 Convergence check: Either converges or reaches the maximum number of iterations
        } while (!isConverged && iter <= iterations);
    }


    private double updatePR(int page, int adjacentPage, int U) {
        // page--->adjacentPage
        double oldPR;
        double newPR;
        if (adjacentPage != -1) {
            oldPR = db.getPR(adjacentPage);
            newPR = oldPR + (d * (db.getPR(page) / U)) + (1 - d) / N;
            db.setPR(adjacentPage, newPR);
        } else {
            oldPR = db.getPR(page);
            newPR = oldPR + (d * (oldPR / U)) + (1 - d) / N;
            db.setPR(page, newPR);
        }
        return Math.abs(newPR - oldPR);
    }


    private void buildWebGraph() throws SQLException {
        int debugging = 0;
        while (this.resultSet.next()) {
            try {
                System.out.println(++debugging);
                int from = resultSet.getInt("id");
                String url = resultSet.getString("url");
                Pivot p = new Pivot(url);
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.body().select("a[href]");
                for (Element link : links) {
                    String childURL;
                    //= link.attr("href");
                    if(link.attr("href").startsWith("//")){
                        childURL = "https:"+link.attr("href");
                    }
                    else if(link.attr("href").startsWith("/")){
                        childURL = p.pivotRootDirectory()+link.attr("href").substring(1);
                    }
                    else {
                        childURL = link.attr("href");
                    }
                    ResultSet resultSet1 = this.db.readID(childURL);

                    if (resultSet1.next()) {
                        int to = resultSet1.getInt("id");
                        this.graph.addDirectedEdge(from, to);
                    }
                }
            } catch (HttpStatusException e) {
                //do nothing
            } catch (Exception e){
                resultSet.previous();
                debugging--;
            }
        }
    }


    static public class WebGraph {
        private ArrayList<ArrayList<Integer>> adj;
        private int[] indegree;
        private int nodes;
        private int edges;

        public WebGraph(int n) {
            this.nodes = n; // Number of rows in the table pages, i.e. number of crawled pages.
            this.edges = 0;
            int v = this.nodes + 1;
            this.indegree = new int[v];
            this.adj = new ArrayList<ArrayList<Integer>>(v);
            adj.add(new ArrayList<Integer>());   // Empty list at position 0, there is no pageID = 0 in the database.
            for (int i = 1; i < v; i++)
                adj.add(new ArrayList<Integer>());

        }

        public void addDirectedEdge(int from, int to) {
            if (!adj.get(from).contains(to)) {
                adj.get(from).add(to);
                indegree[to]++;
                edges++;
            }
        }

        public int indegree(int node) {
            validateNode(node);
            return indegree[node];
        }

        public int outdegree(int node) {
            validateNode(node);
            return adj.get(node).size();
        }

        private void validateNode(int node) {
            if (node < 0 || node > nodes)
                throw new IllegalArgumentException("node " + node + "is out of bounds");
        }

        public void printGraph() {
            int q = this.adj.size(), p, i = 0, j;
            for (; i < q; i++) {
                System.out.println("\nAdjacency list of vertex" + i);
                p = this.adj.get(i).size();
                for (j = 0; j < p; j++) {
                    System.out.print(" -> " + this.adj.get(i).get(j));
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        try {
            PageRank pageRank = new PageRank();
            // Build the web graph.
            pageRank.buildWebGraph();
            // Display the graph.
            pageRank.graph.printGraph();
            // Build the ranks table.
            pageRank.makePageRanks();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
