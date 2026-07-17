package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.DiasSemana;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Periodos;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "horarios_break")
public class HorariosBreaks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long horariosBreaksId;

    @ElementCollection(targetClass = Periodos.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "horarios_breaks_periodos", joinColumns = @JoinColumn(name = "horarios_breaks_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo")
    private List<Periodos> periodos;


    @ElementCollection(targetClass = DiasSemana.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "horarios_breaks_dias", joinColumns = @JoinColumn(name = "horarios_breaks_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private List<DiasSemana> diasSemana;

    @Column(name = "breaks_por_hora", nullable = false)
    private Integer breaksPorHora; // ex: 4 (significa breaks de 15 em 15 minutos)

    @Column(name = "tempo_breaks", nullable = false)
    private Integer tempoBreaks; // ex: 3 (tempo de duração do bloco em minutos)
}