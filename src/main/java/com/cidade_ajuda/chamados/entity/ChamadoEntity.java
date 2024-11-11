package com.cidade_ajuda.chamados.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "chamados")
@Getter @Setter @NoArgsConstructor
public class ChamadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chamado", nullable = false, unique = true)
    private Long id_chamado;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private UserEntity usuario;

    @ManyToOne(optional = true)
    @JoinColumn(name = "atendente", nullable = true)
    private UserEntity atendente;

    @Column(name = "data_chamado", nullable = false)
    private String data_chamado;

    @Column(name = "data_atualizacao", nullable = false)
    private String data_atualizacao;

    @JoinColumn(name = "status", nullable = false)
    private Status statusChamado;

    public enum Status{Aberto, Andamento, Concluido, Encerrado};
}