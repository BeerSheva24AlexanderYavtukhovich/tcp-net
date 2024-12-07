package telran.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONObject;

public class TcpClientServerSession implements Runnable {
    Protocol protocol;
    Socket socket;
    static boolean shutdownNowRequest = false;
    private final DosProtection dosProtection = new DosProtection();
    private boolean rateLimitExceeded = false;
    private boolean notOkResponsesLimitExceeded = false;

    public TcpClientServerSession(Protocol protocol, Socket socket) {
        this.protocol = protocol;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream())) {
            socket.setSoTimeout(TcpConfigurationProperties.SOCKET_INACTIVITY_TIMEOUT);
            String request;
            while ((request = reader.readLine()) != null && !shutdownNowRequest && !rateLimitExceeded
                    && !notOkResponsesLimitExceeded) {

                if (dosProtection.isRateLimitExceeded()) {
                    rateLimitExceeded = true;
                    System.out.println("Potential attack: Too many requests per second."); // for testing
                }
                processRequest(request, writer);

            }
        } catch (SocketTimeoutException e) {
            if (shutdownNowRequest) {
                closeSocket();
            }
        } catch (IOException | RuntimeException e) {
            System.out.println(e.getMessage());
            closeSocket();
        }
    }

    private void processRequest(String request, PrintStream writer) {
        String responseJSON = protocol.getResponseWithJSON(request);
        writer.println(responseJSON);

        Response response = parseResponse(responseJSON);
        if (dosProtection.isNotOkLimitExceeded(response.responseCode())) {
            notOkResponsesLimitExceeded = true;
            System.out.println("Potential atack: Too many not ok responses."); // for testing
        }
    }

  
    private Response parseResponse(String responseJSON) {
        JSONObject jsonObj = new JSONObject(responseJSON);
        ResponseCode responseCode = ResponseCode.valueOf(jsonObj.getString("responseCode"));
        String responseData = jsonObj.getString("responseData");
        return new Response(responseCode, responseData);
    }

    private void closeSocket() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ioEx) {
                System.out.println(ioEx.getMessage());
            }
        }
    }

    public static void shutdownNowRequest() {
        shutdownNowRequest = true;
    }
}
