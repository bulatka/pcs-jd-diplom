import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 8989);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())
        ) {
            out.println("бизнес");
            out.flush();
            String result;
            do {
                result = in.readLine();
                System.out.println(result);
            } while (!result.endsWith("]"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}