package br.com.fiap.nora.services;

import br.com.fiap.nora.dto.MLRequest;
import br.com.fiap.nora.dto.MLResponse;
import br.com.fiap.nora.exceptions.PythonApiException;
import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class MLService {

    // Se ML_PREDICT_URL nao estiver configurado, retorna null — TriagemBO usa fallback (prioridade=baixa).
    public MLResponse predizerUrgencia(MLRequest request) throws PythonApiException {
        String url = System.getenv("ML_PREDICT_URL");
        if (url == null || url.isBlank()) {
            return null;
        }

        Gson gson = new Gson();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(8000)
                .build();

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .disableRedirectHandling()
                .build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(gson.toJson(request), ContentType.APPLICATION_JSON));

            System.out.println("[MLService] POST " + url);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String body = EntityUtils.toString(response.getEntity());
                System.out.println("[MLService] Resposta: " + body);
                return gson.fromJson(body, MLResponse.class);
            }

        } catch (Exception e) {
            throw new PythonApiException("Erro ao chamar ML: " + e.getMessage(), e);
        }
    }
}
