package telran.net;

public interface TcpConfigurationProperties {
    String REQUEST_TYPE_FIELD = "requestType";
    String REQUEST_DATA_FIELD = "requestData";
    String RESPONSE_CODE_FIELD = "responseCode";
    String RESPONSE_DATA_FIELD = "responseData";
    int DEFAULT_INTERVAL_CONNECTION = 3000;
    int DEFAULT_TRIALS_NUMBER_CONNECTION = 10;
    int DEFAULT_SOCKET_TIMEOUT = 10;
    int DEFAULT_IDLE_CONNECTION_TIMEOUT = 60000;
    int DEFAULT_LIMIT_REQUESTS_PER_SEC = 5;
    int DEFAULT_LIMIT_NON_OK_RESPONSES_IN_ROW = 10;
}
