package com.cidade_ajuda.chamados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cidade_ajuda.chamados.entity.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    @Query("SELECT c FROM chat c WHERE c.id_chamado.id_chamado = :idChamado")
    List<ChatEntity> findAllByChamadoId(@Param("idChamado") Long idChamado);
}
