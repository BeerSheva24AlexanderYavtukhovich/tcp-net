package telran.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    private final ExecutorService executorService;
    private boolean isRunning;
    private ServerSocket serverSocket;

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(TcpConfigurationProperties.N_THREADS);
        this.isRunning = false;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(TcpConfigurationProperties.SOCKET_INACTIVITY_TIMEOUT);
            System.out.println("Server is listening on port " + port);
            isRunning = true;
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new TcpClientServerSession(protocol, socket));
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        isRunning = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                // System.out.println("Server socket closed.");//for testing
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        executorService.shutdownNow();
        System.out.println("Shutdown complete");
    }

    public boolean isReady() {
        return isRunning;
    }
}
