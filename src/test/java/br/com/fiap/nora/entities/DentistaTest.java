package br.com.fiap.nora.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DentistaTest {

    @Test
    void verificarDisponibilidade_ativoComVaga_retornaTrue() {
        Dentista d = new Dentista();
        d.setCapMensal(10);
        d.setSttsDent("ativo");
        Assertions.assertTrue(d.verificarDisponibilidade(9));
    }

    @Test
    void verificarDisponibilidade_ativoSemAtivos_retornaTrue() {
        Dentista d = new Dentista();
        d.setCapMensal(10);
        d.setSttsDent("ativo");
        Assertions.assertTrue(d.verificarDisponibilidade(0));
    }

    @Test
    void verificarDisponibilidade_ativoNoLimiteDaCapacidade_retornaFalse() {
        Dentista d = new Dentista();
        d.setCapMensal(10);
        d.setSttsDent("ativo");
        Assertions.assertFalse(d.verificarDisponibilidade(10));
    }

    @Test
    void verificarDisponibilidade_ativoAcimaDaCapacidade_retornaFalse() {
        Dentista d = new Dentista();
        d.setCapMensal(10);
        d.setSttsDent("ativo");
        Assertions.assertFalse(d.verificarDisponibilidade(11));
    }

    @Test
    void verificarDisponibilidade_inativoComVaga_retornaFalse() {
        Dentista d = new Dentista();
        d.setCapMensal(10);
        d.setSttsDent("inativo");
        Assertions.assertFalse(d.verificarDisponibilidade(9));
    }
}
