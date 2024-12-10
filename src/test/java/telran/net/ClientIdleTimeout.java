package telran.net;

public class ClientIdleTimeout {
    public static void main(String[] args) throws InterruptedException {
        TcpClient tcpClient = new TcpClient(TestProps.HOST, TestProps.PORT);

        try {
            tcpClient.sendAndReceive("ok", "");
            Thread.sleep(60000);
            tcpClient.sendAndReceive("ok", "");
            System.out.println("request");

        } catch (RuntimeException e) {
            System.out.println("Server closed connection");

        }

    }

}