package tech.ankanroychowdhury.unifiedpayments.providers.paypal;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PayPalGatewayBuilder {
    private String clientId;
    private String clientSecret;
    private String paypalApiUrl;
    private String accessToken;
    private String apiMethod;
    private Map<String, Object> requestData = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();

    // Private constructor to enforce the use of builder
    private PayPalGatewayBuilder() {}

    public static PayPalGatewayBuilder builder() {
        return new PayPalGatewayBuilder();
    }

    // Builder methods to set client ID, secret, API URL, and API method
    public PayPalGatewayBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public PayPalGatewayBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public PayPalGatewayBuilder paypalApiUrl(String paypalApiUrl) {
        this.paypalApiUrl = paypalApiUrl;
        return this;
    }

    public PayPalGatewayBuilder apiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
        return this;
    }

    // Method to add request data such as currency and amount
    public PayPalGatewayBuilder addRequestData(String key, Object value) {
        this.requestData.put(key, value);
        return this;
    }

    // Internal method to get OAuth access token
    private String getAccessToken() throws IOException {
        if (this.accessToken != null) {
            return this.accessToken;
        }
        String basicAuth = Credentials.basic(clientId, clientSecret);
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();
        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/oauth2/token")
                .post(requestBody)
                .header("Authorization", basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch access token: " + response);
            }
            Map<String, Object> responseMap = objectMapper.readValue(response.body().string(), Map.class);
            this.accessToken = (String) responseMap.get("access_token");
            return this.accessToken;
        }
    }

    // Method to make the API call (like createOrder)
    public Map<String, Object> execute() throws IOException {
        String accessToken = getAccessToken();
        String url;
        RequestBody requestBody;
        // Switch based on API method (you can add more methods here)
        switch (this.apiMethod.toLowerCase()) {
            case "create-order":
                url = paypalApiUrl + "/v2/checkout/orders";
                requestBody = createOrderRequestBody();
                break;
            // More cases for other API methods can be added here.
            default:
                throw new IllegalArgumentException("Invalid API method: " + this.apiMethod);
        }
        // Make the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to call PayPal API: " + response);
            }
            return objectMapper.readValue(response.body().string(), Map.class);
        }
    }

    // Helper method to create the request body for createOrder
    private RequestBody createOrderRequestBody() throws IOException {
        // Default order request details, additional details can be passed via `addRequestData`
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("intent", "CAPTURE");

        // Purchase units are added to the requestData
        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("amount", Map.of(
                "currency_code", requestData.getOrDefault("currency_code", "USD"),
                "value", requestData.getOrDefault("value", "100.00")
        ));
        orderRequest.put("purchase_units", new Map[]{purchaseUnit});
        // Serialize order request to JSON
        String jsonBody = objectMapper.writeValueAsString(orderRequest);
        return RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
    }
}

