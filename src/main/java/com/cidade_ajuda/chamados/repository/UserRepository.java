package com.cidade_ajuda.chamados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cidade_ajuda.chamados.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    
    @Query("SELECT c FROM pessoas c WHERE c.perfilPessoa = :perfil")
    List<UserEntity> findByPerfilPessoa(@Param("perfil") UserEntity.Perfil perfil);
}
