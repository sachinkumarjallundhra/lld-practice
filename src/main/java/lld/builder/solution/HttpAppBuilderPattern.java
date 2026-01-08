package lld.builder.solution;

public class HttpAppBuilderPattern {
    public static void main(String[] args) {
        HttpRequest request1 = new HttpRequest.Builder("https://api.example.com/data")
                .build();

        HttpRequest request2 = new HttpRequest.Builder("https://api.example.com/submit")
                .method("POST")
                .body("{\"key\":\"value\"}")
                .timeout(15000)
                .build();

        HttpRequest request3 = new HttpRequest.Builder("https://api.example.com/config")
                .method("PUT")
                .addHeader("X-API-Key", "secret")
                .addQueryParam("env", "prod")
                .body("config_payload")
                .timeout(5000)
                .build();

        System.out.println(request1);
        System.out.println(request2);
        System.out.println(request3);
    }
}
