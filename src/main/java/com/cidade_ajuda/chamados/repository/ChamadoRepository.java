package com.cidade_ajuda.chamados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cidade_ajuda.chamados.entity.ChamadoEntity;

@Repository
public interface ChamadoRepository extends JpaRepository<ChamadoEntity, Long> {
    @Query("SELECT c FROM chamados c WHERE c.usuario.id_pessoa = :userId")
    List<ChamadoEntity> findByUsuarioId(@Param("userId") Long userId);

    @Query("SELECT c FROM chamados c WHERE c.statusChamado = :status")
    List<ChamadoEntity> findByStatus(@Param("status") ChamadoEntity.Status status);
}
