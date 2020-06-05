package connection;

import com.DbAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.query_engine.Query_Engine;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Launcher  implements HttpHandler {
    static final int  PORT = 4000;
    static final String HOST = "localhost";
    public Launcher(){}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if("GET".equals(httpExchange.getRequestMethod())){
                handleResponse(httpExchange,"GET");
            }else if("POST".equals(httpExchange.getRequestMethod())) {
                handleResponse(httpExchange,"POST");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException, SQLException {

        // Get the search query from the interface
        InputStream inputStream = httpExchange.getRequestBody();
        String searchQueryJSON = new String( inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Convert to JSON
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson g = builder.create();
        SearchQuery sq = g.fromJson(searchQueryJSON, SearchQuery.class);

        //--------------------------------------------------------------------------------------------------------------
        System.out.println(searchQueryJSON);
        System.out.println(sq);
        //--------------------------------------------------------------------------------------------------------------

        // Lookup the database for relevant results
        String response = "";
        if(sq.Trends) {
            response = trendDB(sq.Country);
        } else if(sq.Images) {
            response = imageDB(sq.searchQuery, sq.Country);
        } else {
            response = searchDB(sq.searchQuery, sq.Country);
        }

       sendToInterface(httpExchange, response);
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {

        // Get the search query from the request parameters
        String param = httpExchange.getRequestURI().getQuery();
        System.out.println(param);

        // Lookup the database for suggestions
        String query = param.substring(6, param.length());
        if(!query.isEmpty()) {
            String suggestions = suggestionsDB(query);

            // Send
            sendToInterface(httpExchange, suggestions);
        }

    }

    private void sendToInterface(HttpExchange httpExchange, String response) throws IOException {
        // Send the results to the interface
        OutputStream outputStream = httpExchange.getResponseBody();

        // Set request headers
        Headers headers  = httpExchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, 0);

        // Send
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void handleResponse(HttpExchange httpExchange, String requestType) throws IOException, SQLException {

        if(requestType.toUpperCase().equals("POST")) {
            handlePost(httpExchange);
        }else if(requestType.toUpperCase().equals("GET")) {
            handleGet(httpExchange);
        }

    }

    private String suggestionsDB(String query) {
        DbAdapter db = new DbAdapter();
        ResultSet resultSet = db.getSuggestions(query);
        List<String> suggestions = new ArrayList<>();
        while (true) {
            try {
                if (!resultSet.next()) break;
                String value = resultSet.getString(2);
                System.out.println("--------------------------------------------");
                System.out.println("Suggestion: "+value);
                System.out.println("--------------------------------------------\n\n");
                suggestions.add(value);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return (new Gson()).toJson(suggestions);
    }

    private String imageDB(String searchQuery, String Country) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        ArrayList<String> snippets = new ArrayList<>();
        ResultSet resultSet = qe.processQuery(searchQuery, Country, true,snippets);
        List<Image> data = new ArrayList<>();
        while (resultSet.next()) {
            // 1: src, 2: url
            String src = resultSet.getString(1);
            String url = resultSet.getString(2);
            System.out.println("--------------------------------------------");
            System.out.println("Image src: "+src);
            System.out.println("Image url: "+url);
            System.out.println("--------------------------------------------\n\n");
            data.add(new Image(src, url));
        }
        return (new Gson()).toJson(data);
    }

    private String trendDB(String Country) throws SQLException {
        DbAdapter db = new DbAdapter();

        ResultSet resultSet = db.getTrends(Country);
        List<Trend> data = new ArrayList<>();
        while (resultSet.next()) {
            // 1: id, 2: name
            int id      = resultSet.getInt(1);
            String name = resultSet.getString(2);
            int frequency = resultSet.getInt(4);
            System.out.println("--------------------------------------------");
            System.out.println(id);
            System.out.println(name);
            System.out.println(frequency);
            System.out.println("--------------------------------------------\n\n");
            data.add(new Trend(id, name, frequency));
        }
        return (new Gson()).toJson(data);
    }

    private String searchDB(String searchQuery, String Country) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);
        ArrayList<String> snippets = new ArrayList<>();
        ResultSet resultSet = qe.processQuery(searchQuery, Country, false,snippets);
        List<Result> data = new ArrayList<>();
        int dummyIndexForSnippets=0;
        while (resultSet.next()) {
            // 1: id, 2: url, 3: title, 4: body
            int id    = resultSet.getInt(1);
            String url   = resultSet.getString(2);
            String title = resultSet.getString(3);
            //String body  = resultSet.getString(4).substring(200, 500);

            String body = snippets.get(dummyIndexForSnippets).substring(0,Math.min(300,snippets.get(dummyIndexForSnippets).length()-1));
            dummyIndexForSnippets++;

            System.out.println("--------------------------------------------");
            System.out.println(id);
            System.out.println(url);
            System.out.println(title);
            System.out.println("--------------------------------------------\n\n");
            data.add(new Result(id, url, title, body));
        }
        return (new Gson()).toJson(data);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
        // Unlimited number of threads to process multiple requests concurrently
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/results", new Launcher());
        server.start();
        System.out.println("Server started on port "+ PORT);
    }

    private static class SearchQuery {
        String searchQuery;
        String Country;
        boolean Trends;
        boolean Images;

        @Override
        public String toString() {
            return "{" +
                    "searchQuery='" + searchQuery + '\'' +
                    "Country='" + Country + '\'' +
                    "Trends='" + Trends + '\'' +
                    "Images='" + Images + '\'' +
                    '}';
        }
    }
    private static class Result {
        int id;
        String url;
        String title;
        String body;

        public Result(int id, String url, String title, String body) {
            this.id = id;
            this.url = url;
            this.title = title;
            this.body = body;
        }
    }
    private static class Trend {
        int id;
        String name;
        int frequency;

        public Trend(int id, String name, int frequency) {
            this.id = id;
            this.name = name;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "Trend{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", frequency='" + frequency + '\'' +
                    '}';
        }
    }
    private static class Image {
        String src;
        String url;

        public Image(String src, String url) {
            this.src = src;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "src='" + src + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
