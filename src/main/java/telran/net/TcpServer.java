package telran.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    ExecutorService executorService = Executors.newFixedThreadPool(TcpConfigurationProperties.N_THREADS);

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(TcpConfigurationProperties.SOCKET_INACTIVITY_TIMEOUT);
            System.out.println("Server is listening on port " + port);

            while (!executorService.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new TcpClientServerSession(protocol, socket));
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        executorService.shutdownNow();
        System.out.println("Shutdown complete");
    }
}
