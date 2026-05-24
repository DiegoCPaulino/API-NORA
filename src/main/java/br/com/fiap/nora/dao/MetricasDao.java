package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetricasDao {

    // Cinco contagens em um round-trip: leads, aprovados, encaminhamentos, dentistas ativos, concluídos
    private static final String SQL_RESUMO =
            "SELECT 'totalLeads' AS tipo, COUNT(*) AS total FROM pessoa " +
            "UNION ALL SELECT 'totalAprovados', COUNT(*) FROM pessoa WHERE stts_pess = 'aprovada' " +
            "UNION ALL SELECT 'totalEncaminhamentos', COUNT(*) FROM encaminhamento " +
            "UNION ALL SELECT 'totalDentistasAtivos', COUNT(*) FROM dentista WHERE stts_dent = 'ativo' " +
            "UNION ALL SELECT 'totalConcluidos', COUNT(*) FROM encaminhamento WHERE stts_encam = 'concluido'";

    // DDL: stts_triag IN ('em_analise','aprovada','encerrada','inativa')
    private static final String SQL_TRIAGENS_POR_STATUS =
            "SELECT stts_triag, COUNT(*) AS total FROM triagem GROUP BY stts_triag ORDER BY stts_triag";

    // DDL: prior_encam IN ('baixa','media','alta','urgente')
    private static final String SQL_ENCAM_POR_PRIORIDADE =
            "SELECT prior_encam, COUNT(*) AS total FROM encaminhamento GROUP BY prior_encam ORDER BY prior_encam";

    // DDL: canal_orig IN ('whatsapp','instagram','facebook','telegram','outro')
    private static final String SQL_LEADS_POR_CANAL =
            "SELECT canal_orig, COUNT(*) AS total FROM pessoa GROUP BY canal_orig ORDER BY canal_orig";

    // Agrupa por mes (01-12) somando todos os anos — BO converte para abreviacao em portugues
    private static final String SQL_LEADS_POR_MES =
            "SELECT TO_CHAR(dt_cad, 'MM') AS mes, COUNT(*) AS total " +
            "FROM pessoa GROUP BY TO_CHAR(dt_cad, 'MM') ORDER BY mes";

    // LEFT JOIN para incluir pessoas sem endereco; NVL trata bairro nulo → 'Nao informado'
    private static final String SQL_POR_REGIAO =
            "SELECT NVL(e.bairro, 'Nao informado') AS bairro, COUNT(*) AS total " +
            "FROM pessoa p LEFT JOIN endereco e ON e.id_end = p.fk_end_id " +
            "GROUP BY NVL(e.bairro, 'Nao informado') ORDER BY total DESC";

    public Connection minhaConexao;

    public MetricasDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public MetricasDao(Connection conn) {
        this.minhaConexao = conn;
    }

    public Map<String, Long> contarResumo() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_RESUMO);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("tipo"), rs.getLong("total"));
            }
        }
        return resultado;
    }

    public Map<String, Long> contarTriagensPorStatus() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_TRIAGENS_POR_STATUS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("stts_triag"), rs.getLong("total"));
            }
        }
        return resultado;
    }

    public Map<String, Long> contarEncaminhamentosPorPrioridade() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ENCAM_POR_PRIORIDADE);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("prior_encam"), rs.getLong("total"));
            }
        }
        return resultado;
    }

    public Map<String, Long> contarLeadsPorCanal() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_LEADS_POR_CANAL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("canal_orig"), rs.getLong("total"));
            }
        }
        return resultado;
    }

    // Retorna "01"→N, "02"→N, ... BO mapeia para abreviacao em portugues
    public Map<String, Long> contarLeadsPorMes() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_LEADS_POR_MES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("mes"), rs.getLong("total"));
            }
        }
        return resultado;
    }

    public Map<String, Long> contarPorRegiao() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_POR_REGIAO);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("bairro"), rs.getLong("total"));
            }
        }
        return resultado;
    }
}
