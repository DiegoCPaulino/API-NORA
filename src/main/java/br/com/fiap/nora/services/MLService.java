package br.com.fiap.nora.services;

import br.com.fiap.nora.dto.MLRequest;
import br.com.fiap.nora.dto.MLResponse;
import br.com.fiap.nora.exceptions.PythonApiException;

// Stub — a API Python nao expoe endpoint de predicao ML (pendencia §11.1 CLAUDE.md)
// Substituir quando o endpoint for confirmado e publicado
public class MLService {

    public MLResponse predizerUrgencia(MLRequest request) throws PythonApiException {
        throw new PythonApiException("Endpoint de predicao ML nao definido na API Python.");
    }
}
