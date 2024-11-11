package com.cidade_ajuda.chamados.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "pessoas")
@Getter @Setter @NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pessoa", nullable = true, unique = true)
    private Long id_pessoa;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "sobrenome", nullable = true)
    private String sobrenome;

    @Column(name = "cpf", nullable = true)
    private String cpf;

    @Column(name = "telefone", nullable = true)
    private String telefone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nascimento", nullable = true)
    private String nascimento;

    @Column(name = "cep", nullable = true)
    private String cep;

    @Column(name = "rua", nullable = true)
    private String rua;

    @Column(name = "numero", nullable = true)
    private Integer numero;

    @Column(name = "bairro", nullable = true)
    private String bairro;

    @Column(name = "estado", nullable = true)
    private String estado;

    @Column(name = "cidade", nullable = true)
    private String cidade;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "perfil", nullable = true)
    @Enumerated(EnumType.STRING)
    private Perfil perfilPessoa;

    public enum Perfil {USUARIO, ATENDENTE};
    
}
