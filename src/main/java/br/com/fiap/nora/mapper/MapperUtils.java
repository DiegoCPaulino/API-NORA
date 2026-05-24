package br.com.fiap.nora.mapper;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class MapperUtils {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MapperUtils() {}

    public static String formatarData(LocalDate data) {
        if (data == null) return null;
        return data.format(FORMATO_DATA);
    }

    public static int calcularIdade(LocalDate dtNasc) {
        if (dtNasc == null) return 0;
        return Period.between(dtNasc, LocalDate.now()).getYears();
    }

    public static boolean simNaoParaBoolean(String valor) {
        return "S".equalsIgnoreCase(valor);
    }

    // cadastro → pretriagem; acomp_paciente / acomp_dentista → followup
    public static String mapearCamada(String contexto) {
        if ("acomp_paciente".equals(contexto) || "acomp_dentista".equals(contexto)) {
            return "followup";
        }
        return "pretriagem";
    }
}
