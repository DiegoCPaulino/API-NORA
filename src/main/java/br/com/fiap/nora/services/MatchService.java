package br.com.fiap.nora.services;

import br.com.fiap.nora.dto.MatchResponse;
import br.com.fiap.nora.dto.PythonEnvelope;
import br.com.fiap.nora.exceptions.PythonApiException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MatchService {

    // Endpoint correto para o fluxo de aprovacao: usa ID_TRIAGEM (ja persistida)
    // Motivo: paciente criado durante aprovacao ainda nao recebeu commit e nao e visivel para o Python
    public MatchResponse sugerirDentistaPorTriagem(long idTriagem) throws PythonApiException {
        PythonApiClient client = new PythonApiClient();

        System.out.println("[MatchService] Chamando /api/triagens/" + idTriagem + "/sugestao-dentista");

        String json = client.get("/api/triagens/" + idTriagem + "/sugestao-dentista");

        Type tipo = new TypeToken<PythonEnvelope<MatchResponse>>() {}.getType();
        PythonEnvelope<MatchResponse> envelope = client.getGson().fromJson(json, tipo);

        if (envelope == null) {
            throw new PythonApiException("Resposta vazia do servico de match geografico.");
        }
        if (!envelope.isStatus() || envelope.getData() == null) {
            String msg = envelope.getMessage() != null ? envelope.getMessage() : "Resposta invalida do match geografico.";
            throw new PythonApiException(msg);
        }

        return envelope.getData();
    }

    // Mantido para uso com pacientes ja persistidos (fora do fluxo de aprovacao)
    public MatchResponse sugerirDentista(long idPaciente) throws PythonApiException {
        PythonApiClient client = new PythonApiClient();

        String json = client.get("/api/pacientes/" + idPaciente + "/sugestao-dentista");

        Type tipo = new TypeToken<PythonEnvelope<MatchResponse>>() {}.getType();
        PythonEnvelope<MatchResponse> envelope = client.getGson().fromJson(json, tipo);

        if (envelope == null || envelope.getData() == null) {
            throw new PythonApiException("Resposta invalida do servico de match geografico.");
        }

        return envelope.getData();
    }
}
