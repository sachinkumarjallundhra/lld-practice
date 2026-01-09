package lld.designpattren.builder.problem;

import java.util.Map;

public class HttpAppTelescoping {
    public static void main(String[] args) {
        HttpRequestTelescoping req1 = new HttpRequestTelescoping("https://api.example.com/data");

        HttpRequestTelescoping req2 = new HttpRequestTelescoping(
                "https://api.example.com/submit",
                "POST",
                null,
                null,
                "{\"key\":\"value\"}"
        );

        HttpRequestTelescoping req3 = new HttpRequestTelescoping(
                "https://api.example.com/config",
                "PUT",
                Map.of("X-API-Key", "secret"),
                null,
                "config_data",
                5000
        );
    }
}
