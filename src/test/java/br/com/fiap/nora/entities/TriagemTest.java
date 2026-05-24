package br.com.fiap.nora.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TriagemTest {

    @Test
    void definirElegibilidade_idade11_retornaElegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("elegivel", t.definirElegibilidade(11));
    }

    @Test
    void definirElegibilidade_idade12_retornaElegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("elegivel", t.definirElegibilidade(12));
    }

    @Test
    void definirElegibilidade_idade14_retornaElegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("elegivel", t.definirElegibilidade(14));
    }

    @Test
    void definirElegibilidade_idade16_retornaElegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("elegivel", t.definirElegibilidade(16));
    }

    @Test
    void definirElegibilidade_idade17_retornaElegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("elegivel", t.definirElegibilidade(17));
    }

    @Test
    void definirElegibilidade_idade10_retornaInelegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("inelegivel", t.definirElegibilidade(10));
    }

    @Test
    void definirElegibilidade_idade18_retornaInelegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("inelegivel", t.definirElegibilidade(18));
    }

    @Test
    void definirElegibilidade_idade0_retornaInelegivel() {
        Triagem t = new Triagem();
        Assertions.assertEquals("inelegivel", t.definirElegibilidade(0));
    }

    @Test
    void calcularPrioridade_nivel4ponto0_retornaUrgente() {
        Triagem t = new Triagem();
        Assertions.assertEquals("urgente", t.calcularPrioridade(4.0));
    }

    @Test
    void calcularPrioridade_nivel4ponto5_retornaUrgente() {
        Triagem t = new Triagem();
        Assertions.assertEquals("urgente", t.calcularPrioridade(4.5));
    }

    @Test
    void calcularPrioridade_nivel3ponto0_retornaAlta() {
        Triagem t = new Triagem();
        Assertions.assertEquals("alta", t.calcularPrioridade(3.0));
    }

    @Test
    void calcularPrioridade_nivel3ponto9_retornaAlta() {
        Triagem t = new Triagem();
        Assertions.assertEquals("alta", t.calcularPrioridade(3.9));
    }

    @Test
    void calcularPrioridade_nivel2ponto0_retornaMedia() {
        Triagem t = new Triagem();
        Assertions.assertEquals("media", t.calcularPrioridade(2.0));
    }

    @Test
    void calcularPrioridade_nivel2ponto9_retornaMedia() {
        Triagem t = new Triagem();
        Assertions.assertEquals("media", t.calcularPrioridade(2.9));
    }

    @Test
    void calcularPrioridade_nivel1ponto5_retornaBaixa() {
        Triagem t = new Triagem();
        Assertions.assertEquals("baixa", t.calcularPrioridade(1.5));
    }

    @Test
    void calcularPrioridade_nivel0ponto0_retornaBaixa() {
        Triagem t = new Triagem();
        Assertions.assertEquals("baixa", t.calcularPrioridade(0.0));
    }
}
