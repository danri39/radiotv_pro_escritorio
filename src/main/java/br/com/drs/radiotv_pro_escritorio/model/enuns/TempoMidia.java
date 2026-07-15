package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TempoMidia {

    CINCO("05"),
    SETE("07"),
    DEZ("10"),
    QUINZE("15"),
    VINTE("20"),
    TRINTA("30"),
    QUARENTA("40"),
    QUARENTA_CINCO("45"),
    SESSENTA("60"),
    SETENTA_CINCO("65"),
    NOVENTA("90"),
    CENTO_VINTE("120");

    private String descricao;

    public static TempoMidia converterString(String texto) {
        if (texto == null) return null;
        // Remove tudo que não for dígito
        String digits = texto.replaceAll("\\D", "");
        if (digits.isEmpty()) return null;
        int value;
        try {
            value = Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }

        switch (value) {
            case 5: return CINCO;
            case 7: return SETE;
            case 10: return DEZ;
            case 15: return QUINZE;
            case 20: return VINTE;
            case 30: return TRINTA;
            case 40: return QUARENTA;
            case 45: return QUARENTA_CINCO;
            case 60: return SESSENTA;
            case 75: return SETENTA_CINCO;
            case 90: return NOVENTA;
            case 120: return CENTO_VINTE;
            default: return null;
        }
    }
}