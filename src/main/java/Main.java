import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Objects.nonNull;

public class Main {
    public static void main(String[] args) throws Exception {
        SearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        try (ServerSocket serverSocket = new ServerSocket(8989)) {

            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    String word = in.readLine();
                    if (nonNull(word)) {
                        List<PageEntry> wordFrequency = engine.search(word);
                        StringBuilder sb = new StringBuilder();
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String s = gson.toJson(wordFrequency);
                        out.println(s);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу запустить сервер");
            e.printStackTrace();
        }
    }
}