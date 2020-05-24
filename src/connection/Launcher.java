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
import java.util.Arrays;
import java.util.List;

public class Launcher  implements HttpHandler {
    static final int  PORT = 4000;
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

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson g = builder.create();
        SearchQuery sq = g.fromJson(searchQueryJSON, SearchQuery.class);

        System.out.println(searchQueryJSON);
        System.out.println(sq);
        // Lookup the database for relevant results
        String results1 = searchDB(sq.searchQuery1);
//        String results =
//              "[   {\n" +
//                      "      \"id\":1,\n" +
//                      "      \"title\":\"Mostafa\",\n" +
//                      "      \"url\":\"https://www.google.com/\",\n" +
//                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":2,\n" +
//                      "      \"title\":\"Karim\",\n" +
//                      "      \"url\":\"https://www.youtube.com/\",\n" +
//                      "      \"body\":\"EL motanamer el kaseer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":3,\n" +
//                      "      \"title\":\"Youssef\",\n" +
//                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
//                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":4,\n" +
//                      "      \"title\":\"Amir\",\n" +
//                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
//                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":5,\n" +
//                      "      \"title\":\"Karim\",\n" +
//                      "      \"url\":\"https://www.youtube.com/\",\n" +
//                      "      \"body\":\"EL motanamer el kaseer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":6,\n" +
//                      "      \"title\":\"Youssef\",\n" +
//                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
//                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":7,\n" +
//                      "      \"title\":\"Amir\",\n" +
//                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
//                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":8,\n" +
//                      "      \"title\":\"Mostafa\",\n" +
//                      "      \"url\":\"https://www.google.com/\",\n" +
//                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":8,\n" +
//                      "      \"title\":\"Karim\",\n" +
//                      "      \"url\":\"https://www.youtube.com/\",\n" +
//                      "      \"body\":\"EL motanamer el kaseer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":9,\n" +
//                      "      \"title\":\"Youssef\",\n" +
//                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
//                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":10,\n" +
//                      "      \"title\":\"Amir\",\n" +
//                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
//                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":11,\n" +
//                      "      \"title\":\"Mostafa\",\n" +
//                      "      \"url\":\"https://www.google.com/\",\n" +
//                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":12,\n" +
//                      "      \"title\":\"Karim\",\n" +
//                      "      \"url\":\"https://www.youtube.com/\",\n" +
//                      "      \"body\":\"EL motanamer el kaseer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":13,\n" +
//                      "      \"title\":\"Youssef\",\n" +
//                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
//                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
//                      "   \n" +
//                      "},\n" +
//                      "   {\n" +
//                      "      \"id\":14,\n" +
//                      "      \"title\":\"Amir\",\n" +
//                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
//                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
//                      "   \n" +
//                      "}\n" +
//                      "]";
        // Send the results to the interface
        OutputStream outputStream = httpExchange.getResponseBody();
//        StringBuilder json = new StringBuilder();
//        json.append(results);
//        String s = json.toString();
        String s = results1;

        Headers headers  = httpExchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Content-Type", "application/json");
//        httpExchange.sendResponseHeaders(200, s.length());
        httpExchange.sendResponseHeaders(200, 0);

        outputStream.write(s.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void handleGet(HttpExchange httpExchange) {
    }

    private void handleResponse(HttpExchange httpExchange, String requestType) throws IOException, SQLException {

        if(requestType.toUpperCase().equals("POST")) {
            handlePost(httpExchange);
        }else if(requestType.toUpperCase().equals("GET")) {
            handleGet(httpExchange);
        }

    }

    private String searchDB(String searchQuery) throws SQLException {
        DbAdapter db = new DbAdapter();
        Query_Engine qe = new Query_Engine(db);

        //  @todo Set this boolean for images search lama yet3emel men el interface @AMIR
        boolean imagesSearch = false;
        ResultSet resultSet = qe.processQuery(searchQuery, "Egypt", imagesSearch);
        List<Result> data = new ArrayList<>();
        while (resultSet.next()) {
            // 0: id, 1: url, 2: title, 3: body
            int id    = resultSet.getInt(1);
            String url   = resultSet.getString(2);
            String title = resultSet.getString(3);
            String body  = resultSet.getString(4).substring(200, 500);
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
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/results", new Launcher());
//        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println("Server started on port "+ PORT);
    }

    private static class SearchQuery {
        String searchQuery1;

        @Override
        public String toString() {
            return "SearchQuery{" +
                    "searchQuery1='" + searchQuery1 + '\'' +
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
}
