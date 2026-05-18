package br.com.fiap.nora.services;

import br.com.fiap.nora.bo.AcompEventoBO;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.entities.Encaminhamento;
import com.google.gson.Gson;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// @ApplicationScoped é REQUISITO técnico do @Scheduled do Quarkus — única classe CDI do projeto.
// Extensão aceitável declarada conforme CLAUDE.md §4.2 (bônus de maturidade arquitetural).
// EncaminhamentoDao e AcompEventoBO são instanciados por new, seguindo o padrão do projeto.
@ApplicationScoped
public class FollowUpService {

    private final Gson gson = new Gson();
    private final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .build();

    @Scheduled(cron = "0 0 9 * * ?")
    public void executarFollowUpDiario() {
        String webhookUrl = System.getenv("N8N_WEBHOOK_FOLLOWUP_URL");
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[FollowUpService] N8N_WEBHOOK_FOLLOWUP_URL ausente — modo passivo, nenhum webhook disparado.");
            return;
        }

        LocalDate hoje = LocalDate.now();
        System.out.println("[FollowUpService] Iniciando follow-up automatico para data " + hoje);

        List<Encaminhamento> elegiveis;
        try {
            elegiveis = new EncaminhamentoDao().listarParaFollowUp(hoje);
        } catch (Exception e) {
            System.err.println("[FollowUpService] Erro ao buscar encaminhamentos: " + e.getMessage());
            return;
        }

        System.out.println("[FollowUpService] Encaminhamentos encontrados: " + elegiveis.size());

        for (Encaminhamento enc : elegiveis) {
            // Isola falhas por encaminhamento — erro em 1 nao interrompe os demais
            try {
                enviarWebhook(webhookUrl, enc);
                registrarEvento(enc.getIdEncaminhamento());
                System.out.println("[FollowUpService] idEncaminhamento=" + enc.getIdEncaminhamento() + " processado.");
            } catch (Exception e) {
                System.err.println("[FollowUpService] Falha no idEncaminhamento=" + enc.getIdEncaminhamento() + ": " + e.getMessage());
            }
        }

        System.out.println("[FollowUpService] Follow-up concluido.");
    }

    private void enviarWebhook(String url, Encaminhamento enc) throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("idEncaminhamento", enc.getIdEncaminhamento());
        payload.put("idPaciente", enc.getIdPaciente());
        payload.put("idDentista", enc.getIdDentista());
        payload.put("dtEncam", enc.getDtEncam() != null ? enc.getDtEncam().toString() : null);
        payload.put("prevFollow", enc.getPrevFollow() != null ? enc.getPrevFollow().toString() : null);
        payload.put("sttsEncam", enc.getSttsEncam());
        payload.put("distKm", enc.getDistKm());

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .disableRedirectHandling()
                .build()) {
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(gson.toJson(payload), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getEntity() != null) EntityUtils.toString(response.getEntity());
            }
        }
    }

    private void registrarEvento(long idEncaminhamento) throws Exception {
        AcompEvento evento = new AcompEvento();
        evento.setIdEncaminhamento(idEncaminhamento);
        evento.setTipoEvento("follow_up");
        evento.setDsEvento("Follow-up automatico disparado para N8N.");
        evento.setOrigem("sistema");
        // tipoMensagem deixado null — campo opcional conforme CHK_ACOMP_MSG
        new AcompEventoBO().criar(evento);
    }
}
