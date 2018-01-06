import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import  java.net.InetSocketAddress;
import com.sun.net.httpserver.*;

public class JavaHttpServer {

    public static void main(String args[]) throws IOException {
        HttpServer[] servers = new HttpServer[5];
        int i = 0;
        for (HttpServer server : servers) {
            server = HttpServer.create(new InetSocketAddress(9000 + i), 0);
            server.createContext("/runService", new handlerOne());
            server.createContext("/exit", new exitHandler());
            server.setExecutor(null);//Default executor
            server.start();
            i++;
        }
    }

    private static class exitHandler implements HttpHandler {

        @Override//Method part of HttpHandler interface
        public void handle(HttpExchange httpExchange) throws IOException {
            System.exit(0);
        }
    }

    private static class handlerOne implements HttpHandler {

        @Override//Method part of HttpHandler interface
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println(httpExchange.getRequestMethod());
            String commandResult = executeCommand("ls -l -a");
            System.out.println(commandResult);
            String answer = commandResult;
            httpExchange.sendResponseHeaders(200, answer.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(answer.getBytes());
            os.close();
        }
    }

    public static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


}
