package connection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Launcher  implements HttpHandler {
    public URL url;
    public Launcher(){
        try {
//            url = new URL("http://localhost:3000");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer content = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                content.append(inputLine);
//
//            }
//            System.out.println(content);
//            in.close();

        }catch (Exception ignored){}
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue = null;
//        if("GET".equals(httpExchange.getRequestMethod())){
//            requestParamValue =handleGet(httpExchange);
//        }else if("POST".equals(httpExchange.getRequestMethod())) {
//            requestParamValue =handlePost(httpExchange);
//        }
        handleResponse(httpExchange,requestParamValue);
    }

//    private String handleGet(HttpExchange httpExchange) {
//        //return httpExchange.getResponseBody()
//    }
//
//    private String handlePost(HttpExchange httpExchange) {
//       // return httpExchange.getResponseBody()
//    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder json = new StringBuilder();
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
        json.append(results);
        String s = json.toString();
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        httpExchange.sendResponseHeaders(200, s.length());

        outputStream.write(s.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    public static void main(String[] args) throws IOException {
//        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 4000), 0);
        server.createContext("/results", new Launcher());
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println("Server started on port 4000");

    }
}
