package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ramo_atividade")
public class RamoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ramo_atividade_id;

    private String descricao;
}
