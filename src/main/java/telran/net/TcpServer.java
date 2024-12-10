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
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    int socketTimeout;
    int idleConnectionTimeout;
    int limitRequestsPerSecond;
    int limitNonOkResponsesInRow;

    public TcpServer(Protocol protocol, int port, int socketTimeout, int idleConnectionTimeout,
            int limitRequestsPerSecond, int limitNonOkResponsesInRow) {
        this.protocol = protocol;
        this.port = port;
        this.socketTimeout = socketTimeout;
        this.idleConnectionTimeout = idleConnectionTimeout;
        this.limitRequestsPerSecond = limitRequestsPerSecond;
        this.limitNonOkResponsesInRow = limitNonOkResponsesInRow;
    }

    public TcpServer(Protocol protocol, int port, int idleConnectionTimeout) {
        this(protocol, port, TcpConfigurationProperties.DEFAULT_SOCKET_TIMEOUT, idleConnectionTimeout,
                TcpConfigurationProperties.DEFAULT_LIMIT_REQUESTS_PER_SEC,
                TcpConfigurationProperties.DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW);
    }

    public TcpServer(Protocol protocol, int port) {
        this(protocol, port, TcpConfigurationProperties.DEFAULT_SOCKET_TIMEOUT,
                TcpConfigurationProperties.DEFAULT_IDLE_CONNECTION_TIMEOUT,
                TcpConfigurationProperties.DEFAULT_LIMIT_REQUESTS_PER_SEC,
                TcpConfigurationProperties.DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on the port " + port);
            serverSocket.setSoTimeout(socketTimeout);
            while (!executor.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(socketTimeout);
                    var session = new TcpClientServerSession(protocol, socket, this);
                    executor.execute(session);
                } catch (SocketTimeoutException e) {

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }

}
