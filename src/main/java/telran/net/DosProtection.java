package telran.net;

public class DosProtection {
    private int notOkResponses = 0;
    private int requestCount = 0;
    private long lastRequestTimeMillis = System.currentTimeMillis();

    public boolean isRateLimitExceeded() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastRequestTimeMillis > 1000) {
            requestCount = 0;
            lastRequestTimeMillis = currentTimeMillis;
        }
        requestCount++;
        return requestCount > TcpConfigurationProperties.MAX_REQUESTS_PER_SECOND;
    }

    public boolean isNotOkLimitExceeded(ResponseCode responseCode) {
        if (responseCode != ResponseCode.OK) {
            notOkResponses++;
        }
        return notOkResponses > TcpConfigurationProperties.MAX_NOT_OK_RESPONSES;
    }
}
