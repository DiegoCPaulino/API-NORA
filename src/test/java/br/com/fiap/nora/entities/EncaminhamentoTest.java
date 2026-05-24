package br.com.fiap.nora.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EncaminhamentoTest {

    @Test
    void encerrarEncaminhamento_estadoInicial_naoEConcluido() {
        Encaminhamento e = new Encaminhamento();
        Assertions.assertNotEquals("concluido", e.getSttsEncam());
    }

    @Test
    void encerrarEncaminhamento_statusNulo_tornaStatusConcluido() {
        Encaminhamento e = new Encaminhamento();
        e.encerrarEncaminhamento();
        Assertions.assertEquals("concluido", e.getSttsEncam());
    }

    @Test
    void encerrarEncaminhamento_statusAtivo_tornaStatusConcluido() {
        Encaminhamento e = new Encaminhamento();
        e.setSttsEncam("ativo");
        e.encerrarEncaminhamento();
        Assertions.assertEquals("concluido", e.getSttsEncam());
    }
}
