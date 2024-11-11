package com.cidade_ajuda.chamados.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "chat")
@Getter @Setter @NoArgsConstructor
public class ChatEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat", nullable = false, unique = true)
    private Long id_chat;

    @ManyToOne
    @JoinColumn(name = "id_chamado", nullable = false)
    private ChamadoEntity id_chamado;
   
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity id_user;

    @Column(name = "data_chat", nullable = false)
    private String data_chat;

    @Column(name = "hora_chat", nullable = false)
    private String hora_chat;

    @Column(name = "conversa", nullable = false)
    private String conversa;

}
