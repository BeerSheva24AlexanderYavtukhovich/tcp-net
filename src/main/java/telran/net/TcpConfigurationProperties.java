package telran.net;

public interface TcpConfigurationProperties {
    String REQUEST_TYPE_FIELD = "requestType";
    String REQUEST_DATA_FIELD = "requestData";
    String RESPONSE_CODE_FIELD = "responseCode";
    String RESPONSE_DATA_FIELD = "responseData";
    int DEFAULT_INTERVAL_CONNECTION = 3000;
    int DEFAULT_TRIALS_NUMBER_CONNECTION = 10;
    int N_THREADS = 10;
    int SOCKET_INACTIVITY_TIMEOUT = 5;
    int MAX_NOT_OK_RESPONSES = 5;  
    int MAX_REQUESTS_PER_SECOND = 10;
}
