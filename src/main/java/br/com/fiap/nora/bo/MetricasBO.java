package br.com.fiap.nora.bo;

import br.com.fiap.nora.conexoes.ConexaoFactory;
import br.com.fiap.nora.dao.MetricasDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetricasBO {

    private static final String[] MESES_NUM = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    private static final String[] MESES_ABR = {"Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez"};

    public Map<String, Object> resumo() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Map<String, Long> raw = new MetricasDao(conn).contarResumo();
            long totalLeads = raw.getOrDefault("totalLeads", 0L);
            long totalAprovados = raw.getOrDefault("totalAprovados", 0L);
            long totalEncaminhamentos = raw.getOrDefault("totalEncaminhamentos", 0L);
            long totalDentistasAtivos = raw.getOrDefault("totalDentistasAtivos", 0L);
            long totalConcluidos = raw.getOrDefault("totalConcluidos", 0L);

            double taxaAprovacao = totalLeads > 0 ? (double) totalAprovados / totalLeads : 0.0;
            double taxaConclusao = totalEncaminhamentos > 0 ? (double) totalConcluidos / totalEncaminhamentos : 0.0;

            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("totalLeads", totalLeads);
            resultado.put("totalAprovados", totalAprovados);
            resultado.put("totalEncaminhamentos", totalEncaminhamentos);
            resultado.put("totalDentistasAtivos", totalDentistasAtivos);
            resultado.put("taxaAprovacao", taxaAprovacao);
            resultado.put("taxaConclusao", taxaConclusao);
            return resultado;
        }
    }

    // Pre-inicializa as 4 chaves com 0; putAll sobrescreve com valores reais do banco.
    // Status fora das 4 chaves esperadas sao incluidos pelo putAll (nao descartados).
    public Map<String, Long> triagensPorStatus() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Map<String, Long> resultado = new LinkedHashMap<>();
            resultado.put("em_analise", 0L);
            resultado.put("aprovada", 0L);
            resultado.put("encerrada", 0L);
            resultado.put("inativa", 0L);
            resultado.putAll(new MetricasDao(conn).contarTriagensPorStatus());
            return resultado;
        }
    }

    public Map<String, Long> encaminhamentosPorPrioridade() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Map<String, Long> resultado = new LinkedHashMap<>();
            resultado.put("baixa", 0L);
            resultado.put("media", 0L);
            resultado.put("alta", 0L);
            resultado.put("urgente", 0L);
            resultado.putAll(new MetricasDao(conn).contarEncaminhamentosPorPrioridade());
            return resultado;
        }
    }

    public Map<String, Long> leadsPorCanal() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Map<String, Long> resultado = new LinkedHashMap<>();
            resultado.put("telegram", 0L);
            resultado.put("whatsapp", 0L);
            resultado.put("instagram", 0L);
            resultado.put("facebook", 0L);
            resultado.put("outro", 0L);
            resultado.putAll(new MetricasDao(conn).contarLeadsPorCanal());
            return resultado;
        }
    }

    public Map<String, Long> leadsPorMes() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            Map<String, Long> resultado = new LinkedHashMap<>();
            for (String abr : MESES_ABR) resultado.put(abr, 0L);

            Map<String, Long> raw = new MetricasDao(conn).contarLeadsPorMes();
            for (Map.Entry<String, Long> entry : raw.entrySet()) {
                for (int i = 0; i < MESES_NUM.length; i++) {
                    if (MESES_NUM[i].equals(entry.getKey())) {
                        resultado.put(MESES_ABR[i], entry.getValue());
                        break;
                    }
                }
            }
            return resultado;
        }
    }

    public Map<String, Long> regioes() throws SQLException, ClassNotFoundException {
        try (Connection conn = new ConexaoFactory().conexao()) {
            return new MetricasDao(conn).contarPorRegiao();
        }
    }
}
