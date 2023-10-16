import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9090;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public Server() throws IOException {
    }

    public static void main (String [ ] args ) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        while(true) {
            System.out.println(" [ SERVER ] Waiting for client connection ... ");
            Socket client = listener.accept();
            System.out.println(" [ SERVER ] Connected to client ! ");
            ClientHandler clientThread  = new ClientHandler(client);
            // we are adding every Client to ArrayList and handling each Client by Client Handler
            clients.add(clientThread);

            pool.execute(clientThread);
        }
    }
}
