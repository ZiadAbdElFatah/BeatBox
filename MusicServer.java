import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MusicServer {
    final List<ObjectOutputStream> clientOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        new MusicServer().go();
    }

    public void go() {
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            ExecutorService threadPool = Executors.newCachedThreadPool();

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(out);
                threadPool.execute(new ClientHandler(clientSocket));
                System.out.println("got a connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tellEveryone(Object one, Object two) {
        for (ObjectOutputStream out : clientOutputStreams) {
            try {
                out.writeObject(one);
                out.writeObject(two);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ClientHandler implements Runnable {
        private ObjectInputStream in;
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Object beatSequence = null;
            Object userName = null;
            try {
                while ((userName = in.readObject()) != null) {
                    beatSequence = in.readObject();
                    System.out.println("read two objects");
                    tellEveryone(userName, beatSequence);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
