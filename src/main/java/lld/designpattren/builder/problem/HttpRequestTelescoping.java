package lld.designpattren.builder.problem;

import java.util.HashMap;
import java.util.Map;

class HttpRequestTelescoping {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private int timeout;

    public HttpRequestTelescoping(String url) {
        this(url, "GET");
    }

    public HttpRequestTelescoping(String url, String method) {
        this(url, method, null);
    }

    public HttpRequestTelescoping(String url, String method, Map<String, String> headers) {
        this(url, method, headers, null);
    }

    public HttpRequestTelescoping(String url, String method, Map<String, String> headers,
                                  Map<String, String> queryParams) {
        this(url, method, headers, queryParams, null);
    }

    public HttpRequestTelescoping(String url, String method, Map<String, String> headers,
                                  Map<String, String> queryParams, String body) {
        this(url, method, headers, queryParams, body, 30000);
    }

    public HttpRequestTelescoping(String url, String method, Map<String, String> headers,
                                  Map<String, String> queryParams, String body, int timeout) {
        this.url = url;
        this.method = method;
        this.headers = headers == null ? new HashMap<>() : headers;
        this.queryParams = queryParams == null ? new HashMap<>() : queryParams;
        this.body = body;
        this.timeout = timeout;

        System.out.println("HttpRequest Created: URL=" + url +
                ", Method=" + method +
                ", Headers=" + this.headers.size() +
                ", Params=" + this.queryParams.size() +
                ", Body=" + (body != null) +
                ", Timeout=" + timeout);
    }

    // Getters could be added here
}
