package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMusica {

    SUCESSOS("Secessos"),
    FLASHBACK("Flashback"),
    SERTANEJO("Sertanejo"),
    SERTANEJO_UNIVERSITARIO("Sertanejo Universitário"),
    MODAO("Moda de Viola"),
    FORRO("Forró"),
    PISEIRO("Piseiro"),
    ARROCHA("Arrocha"),
    PAGODE("Pagode"),
    SAMBA("Samba"),
    MPB("Música Popular Brasileira"),
    POP("Pop"),
    POP_ROCK("Pop Rock"),
    ROCK("Rock"),
    GOSPEL("Gospel"),
    CATOLICA("Católica"),
    ELETRONICA("Eletrônica"),
    FUNK("Funk"),
    RAP("Rap"),
    HIP_HOP("Hip Hop"),
    TRAP("Trap"),
    REGGAE("Reggae"),
    AXE("Axé"),
    INTERNACIONAL("Internacional"),
    INFANTIL("Infantil"),
    INSTRUMENTAL("Instrumental"),
    CLASSICA("Música Clássica"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    COUNTRY("Country"),
    LATINA("Música Latina"),
    OUTROS("Outros");

    private final String descricao;
}