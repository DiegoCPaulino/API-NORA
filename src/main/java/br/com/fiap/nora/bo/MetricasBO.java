package br.com.fiap.nora.bo;

import br.com.fiap.nora.dao.MetricasDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MetricasBO {

    public Map<String, Long> resumo() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarResumo();
    }

    public List<Map<String, Object>> triagensPorStatus() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarTriagensPorStatus();
    }

    public List<Map<String, Object>> encaminhamentosPorPrioridade() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarEncaminhamentosPorPrioridade();
    }

    public List<Map<String, Object>> leadsPorCanal() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarLeadsPorCanal();
    }

    public List<Map<String, Object>> leadsPorMes() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarLeadsPorMes();
    }

    public List<Map<String, Object>> regioes() throws SQLException, ClassNotFoundException {
        return new MetricasDao().contarPorRegiao();
    }
}
