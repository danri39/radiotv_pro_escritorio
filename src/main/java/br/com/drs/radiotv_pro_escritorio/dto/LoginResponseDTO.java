package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;

    private UsuarioDTO usuario;
}