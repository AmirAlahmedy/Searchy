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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        String searchQueryJSON = new String( inputStream.readAllBytes(), "UTF-8");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson g = builder.create();
        SearchQuery sq = g.fromJson(searchQueryJSON, SearchQuery.class);

        System.out.println(searchQueryJSON);
        System.out.println(sq);
        // Lookup the database for relevant results
//        String results = searchDB(searchQuery);
        String results =
              "[   {\n" +
                      "      \"id\":1,\n" +
                      "      \"title\":\"Mostafa\",\n" +
                      "      \"url\":\"https://www.google.com/\",\n" +
                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":2,\n" +
                      "      \"title\":\"Karim\",\n" +
                      "      \"url\":\"https://www.youtube.com/\",\n" +
                      "      \"body\":\"EL motanamer el kaseer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":3,\n" +
                      "      \"title\":\"Youssef\",\n" +
                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":4,\n" +
                      "      \"title\":\"Amir\",\n" +
                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":5,\n" +
                      "      \"title\":\"Karim\",\n" +
                      "      \"url\":\"https://www.youtube.com/\",\n" +
                      "      \"body\":\"EL motanamer el kaseer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":6,\n" +
                      "      \"title\":\"Youssef\",\n" +
                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":7,\n" +
                      "      \"title\":\"Amir\",\n" +
                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":8,\n" +
                      "      \"title\":\"Mostafa\",\n" +
                      "      \"url\":\"https://www.google.com/\",\n" +
                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":8,\n" +
                      "      \"title\":\"Karim\",\n" +
                      "      \"url\":\"https://www.youtube.com/\",\n" +
                      "      \"body\":\"EL motanamer el kaseer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":9,\n" +
                      "      \"title\":\"Youssef\",\n" +
                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":10,\n" +
                      "      \"title\":\"Amir\",\n" +
                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":11,\n" +
                      "      \"title\":\"Mostafa\",\n" +
                      "      \"url\":\"https://www.google.com/\",\n" +
                      "      \"body\":\"Dah agmad wa7ed feenaaaaa\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":12,\n" +
                      "      \"title\":\"Karim\",\n" +
                      "      \"url\":\"https://www.youtube.com/\",\n" +
                      "      \"body\":\"EL motanamer el kaseer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":13,\n" +
                      "      \"title\":\"Youssef\",\n" +
                      "      \"url\":\"https://std.eng.cu.edu.eg/\",\n" +
                      "      \"body\":\"Byetkalem keteeeeer f hangarab nekteb paragraph kebeeeer le abooooo bench kebeeeeeeeeeeeeeeeeeeeeeeer\"\n" +
                      "   \n" +
                      "},\n" +
                      "   {\n" +
                      "      \"id\":14,\n" +
                      "      \"title\":\"Amir\",\n" +
                      "      \"url\":\"https://www.wikipedia.org/\",\n" +
                      "      \"body\":\"Dah elly hay5alas el project dah\"\n" +
                      "   \n" +
                      "}\n" +
                      "]";
        // Send the results to the interface
        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder json = new StringBuilder();
        json.append(results);
        String s = json.toString();

        Headers headers  = httpExchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Content-Type", "application/json");
//        headers.add("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE");
        httpExchange.sendResponseHeaders(200, s.length());

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
        ResultSet resultSet = qe.processQuery(searchQuery);
        String resultJSON = "";
        while (resultSet.next()) {

        }
        return resultJSON;
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
}
