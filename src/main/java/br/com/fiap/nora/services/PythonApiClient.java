package br.com.fiap.nora.services;

import br.com.fiap.nora.exceptions.PythonApiException;
import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class PythonApiClient {

    private final String baseUrl;
    private final Gson gson = new Gson();
    private final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .build();

    public PythonApiClient() {
        String url = System.getenv("API_PYTHON_BASE_URL");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Variavel de ambiente API_PYTHON_BASE_URL nao configurada.");
        }
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public String get(String path) throws PythonApiException {
        String url = baseUrl + path;

        System.out.println("[PythonApiClient] GET " + url);

        HttpGet request = new HttpGet(url);

        try (
                CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
                CloseableHttpResponse response = httpClient.execute(request)
        ) {
            int statusCode = response.getStatusLine().getStatusCode();

            String body = "";
            if (response.getEntity() != null) {
                body = EntityUtils.toString(response.getEntity());
            }

            System.out.println("[PythonApiClient] Status HTTP: " + statusCode);
            System.out.println("[PythonApiClient] Body recebido: " + body);

            return body;

        } catch (Exception e) {
            System.out.println("[PythonApiClient] Erro GET " + url + ": " + e.getMessage());
            throw new PythonApiException("Erro na chamada GET " + path + ": " + e.getMessage(), e);
        }
    }

    public String post(String path, Object body) throws PythonApiException {
        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .disableRedirectHandling()
                .build()) {

            HttpPost request = new HttpPost(baseUrl + path);
            request.setEntity(new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            throw new PythonApiException("Erro na chamada POST " + path + ": " + e.getMessage(), e);
        }
    }

    public Gson getGson() { return gson; }
}
