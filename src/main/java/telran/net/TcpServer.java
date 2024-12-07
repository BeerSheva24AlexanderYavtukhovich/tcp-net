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
            System.out.println("Server is listening on the port " + port);
            while (!executorService.isShutdown()) {
                try {
                    if (TcpClientServerSession.shutdownNowRequest) {
                        shutdown();
                    } else {
                        Socket socket = serverSocket.accept();
                        if (!TcpClientServerSession.shutdownNowRequest) {
                            executorService.execute(new TcpClientServerSession(protocol, socket));
                        } else {
                            socket.close();
                        }
                    }
                } catch (SocketTimeoutException e) {
                    if (TcpClientServerSession.shutdownNowRequest) {
                        shutdown();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (!executorService.isShutdown()) {
                shutdown();
            }
        }
    }

    public void shutdown() {
        TcpClientServerSession.shutdownNowRequest();
        executorService.shutdownNow();
        System.out.println("Shutdown complete");
    }
}
