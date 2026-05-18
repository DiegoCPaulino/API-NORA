package br.com.fiap.nora.services;

import br.com.fiap.nora.dto.MLRequest;
import br.com.fiap.nora.dto.MLResponse;
import br.com.fiap.nora.exceptions.PythonApiException;

// Stub — a API Python não expõe endpoint de predição ML. Retorna null e usa fallback no TriagemBO.
public class MLService {

    public MLResponse predizerUrgencia(MLRequest request) throws PythonApiException {
        throw new PythonApiException("Endpoint de predicao ML nao definido na API Python.");
    }
}
