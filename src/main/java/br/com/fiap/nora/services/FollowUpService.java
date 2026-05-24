package br.com.fiap.nora.services;

import br.com.fiap.nora.bo.AcompEventoBO;
import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.DentistaDao;
import br.com.fiap.nora.dao.EncaminhamentoDao;
import br.com.fiap.nora.dao.PacienteDao;
import br.com.fiap.nora.dao.PessoaDao;
import br.com.fiap.nora.entities.AcompEvento;
import br.com.fiap.nora.entities.Dentista;
import br.com.fiap.nora.entities.Encaminhamento;
import br.com.fiap.nora.entities.Paciente;
import br.com.fiap.nora.entities.Pessoa;
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

import java.sql.Connection;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// @ApplicationScoped e requisito tecnico do @Scheduled do Quarkus — unica classe CDI do projeto.
// Todos os DAOs/BOs sao instanciados por new, mantendo o padrao academico.
@ApplicationScoped
public class FollowUpService {

    private final Gson gson = new Gson();
    private final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .build();

    @Scheduled(cron = "0 0 9 * * ?")
    public void executarFollowUpDiario() {
        executar();
    }

    // Ponto de entrada publico — chamado pelo scheduler e pelo endpoint manual POST /follow-up/executar.
    public void executar() {
        String webhookUrl = System.getenv("N8N_WEBHOOK_FOLLOWUP_URL");
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[FollowUpService] N8N_WEBHOOK_FOLLOWUP_URL ausente — nenhum webhook disparado.");
            return;
        }

        System.out.println("[FollowUpService] Iniciando follow-up automatico para " + LocalDate.now());

        // Busca elegíveis com uma conexao que e fechada imediatamente apos a leitura
        List<Encaminhamento> elegiveis;
        try (Connection conn = new ConexaoFactory().conexao()) {
            elegiveis = new EncaminhamentoDao(conn).listarParaFollowUp();
        } catch (Exception e) {
            System.err.println("[FollowUpService] Erro ao buscar encaminhamentos: " + e.getMessage());
            return;
        }

        System.out.println("[FollowUpService] Encaminhamentos elegíveis: " + elegiveis.size());

        for (Encaminhamento enc : elegiveis) {
            // Falha em 1 encaminhamento nao interrompe os demais
            try {
                enviarWebhook(webhookUrl, enc);
                registrarEvento(enc.getIdEncam());
                System.out.println("[FollowUpService] idEncam=" + enc.getIdEncam() + " processado.");
            } catch (Exception e) {
                System.err.println("[FollowUpService] Falha no idEncam=" + enc.getIdEncam() + ": " + e.getMessage());
            }
        }

        System.out.println("[FollowUpService] Follow-up concluido.");
    }

    private void enviarWebhook(String url, Encaminhamento enc) throws Exception {
        // Busca dados complementares em uma unica conexao; fechada antes da chamada HTTP
        String pacienteNome = null;
        String telefonePaciente = null;
        String canalOrigem = null;
        String dentistaNome = null;

        try (Connection conn = new ConexaoFactory().conexao()) {
            if (enc.getFkPacId() != null) {
                Paciente pac = new PacienteDao(conn).buscarPorId(enc.getFkPacId());
                if (pac != null && pac.getFkPessId() != null) {
                    Pessoa pessoa = new PessoaDao(conn).buscarPorId(pac.getFkPessId());
                    if (pessoa != null) {
                        pacienteNome = pessoa.getNmPess();
                        telefonePaciente = pessoa.getTelPess();
                        canalOrigem = pessoa.getCanalOrig();
                    }
                }
            }
            if (enc.getFkDentId() != null) {
                Dentista dent = new DentistaDao(conn).buscarPorId(enc.getFkDentId());
                if (dent != null) dentistaNome = dent.getNmDent();
            }
        }
        // Conexao fechada aqui — HTTP executado sem manter sessao Oracle aberta

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("encaminhamentoId", enc.getIdEncam());
        payload.put("pacienteId", enc.getFkPacId());
        payload.put("pacienteNome", pacienteNome);
        payload.put("telefonePaciente", telefonePaciente);
        payload.put("canalOrigem", canalOrigem);
        payload.put("dentistaId", enc.getFkDentId());
        payload.put("dentistaNome", dentistaNome);
        payload.put("previsaoFollowUp", enc.getPrevFollow() != null ? enc.getPrevFollow().toString() : null);
        payload.put("prioridade", enc.getPriorEncam());
        payload.put("origem", "backend-java");

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

    private void registrarEvento(Long idEncam) throws Exception {
        AcompEvento evento = new AcompEvento();
        evento.setFkEncamId(idEncam);
        // tp_evento='followup' e origem='sistema' confirmados no CHECK do DDL (acomp_tp_ck, acomp_orig_ck)
        evento.setTpEvento("followup");
        evento.setDsEvento("Follow-up automatico disparado para N8N.");
        evento.setOrigem("sistema");
        new AcompEventoBO().criar(evento);
    }
}
