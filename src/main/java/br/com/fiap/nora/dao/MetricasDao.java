package br.com.fiap.nora.dao;

import br.com.fiap.nora.conexoes.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MetricasDao {

    // UNION ALL em query única — cinco contagens em um round-trip (coluna TIPO e TOTAL definidas no primeiro SELECT)
    private static final String SQL_RESUMO =
            "SELECT 'pessoas' AS TIPO, COUNT(*) AS TOTAL FROM TB_PESSOA " +
            "UNION ALL SELECT 'triagens', COUNT(*) FROM TB_TRIAGEM " +
            "UNION ALL SELECT 'pacientes', COUNT(*) FROM TB_PACIENTE " +
            "UNION ALL SELECT 'encaminhamentos', COUNT(*) FROM TB_ENCAMINHAMENTO " +
            "UNION ALL SELECT 'conversasAbertas', COUNT(*) FROM TB_CONVERSA WHERE STTS_CONV = 'aberta'";

    // Agrupamento por STTS_TRIAG (DDL: 'em_analise','aprovada','reprovada','cancelada')
    private static final String SQL_TRIAGENS_POR_STATUS =
            "SELECT STTS_TRIAG, COUNT(*) AS TOTAL FROM TB_TRIAGEM GROUP BY STTS_TRIAG ORDER BY STTS_TRIAG";

    // Agrupamento por PRIORIDADE na tabela de encaminhamentos (DDL: 'baixa','media','alta','urgente')
    private static final String SQL_ENCAM_POR_PRIORIDADE =
            "SELECT PRIORIDADE, COUNT(*) AS TOTAL FROM TB_ENCAMINHAMENTO GROUP BY PRIORIDADE ORDER BY PRIORIDADE";

    // Agrupamento por CANAL_ORIG (DDL: 'telegram','web','manual','n8n')
    private static final String SQL_LEADS_POR_CANAL =
            "SELECT CANAL_ORIG, COUNT(*) AS TOTAL FROM TB_PESSOA GROUP BY CANAL_ORIG ORDER BY CANAL_ORIG";

    // TO_CHAR(DATA_CRIACAO,'YYYY-MM') = syntax Oracle padrão
    private static final String SQL_LEADS_POR_MES =
            "SELECT TO_CHAR(DATA_CRIACAO, 'YYYY-MM') AS ANO_MES, COUNT(*) AS TOTAL " +
            "FROM TB_PESSOA GROUP BY TO_CHAR(DATA_CRIACAO, 'YYYY-MM') ORDER BY ANO_MES";

    // Sem coluna REGIAO no DDL — agrupa por UF de TB_ENDERECO via join com TB_PESSOA
    private static final String SQL_POR_REGIAO =
            "SELECT E.UF AS REGIAO, COUNT(*) AS TOTAL " +
            "FROM TB_PESSOA P JOIN TB_ENDERECO E ON E.ID_ENDERECO = P.ID_ENDERECO " +
            "GROUP BY E.UF ORDER BY TOTAL DESC";

    public Connection minhaConexao;

    public MetricasDao() throws SQLException, ClassNotFoundException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public Map<String, Long> contarResumo() throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_RESUMO);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("TIPO"), rs.getLong("TOTAL"));
            }
        }
        return resultado;
    }

    public List<Map<String, Object>> contarTriagensPorStatus() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_TRIAGENS_POR_STATUS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("status", rs.getString("STTS_TRIAG"));
                item.put("total", rs.getLong("TOTAL"));
                lista.add(item);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> contarEncaminhamentosPorPrioridade() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_ENCAM_POR_PRIORIDADE);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("prioridade", rs.getString("PRIORIDADE"));
                item.put("total", rs.getLong("TOTAL"));
                lista.add(item);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> contarLeadsPorCanal() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_LEADS_POR_CANAL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("canal", rs.getString("CANAL_ORIG"));
                item.put("total", rs.getLong("TOTAL"));
                lista.add(item);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> contarLeadsPorMes() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_LEADS_POR_MES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("anoMes", rs.getString("ANO_MES"));
                item.put("total", rs.getLong("TOTAL"));
                lista.add(item);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> contarPorRegiao() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement stmt = minhaConexao.prepareStatement(SQL_POR_REGIAO);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("regiao", rs.getString("REGIAO"));
                item.put("total", rs.getLong("TOTAL"));
                lista.add(item);
            }
        }
        return lista;
    }
}
