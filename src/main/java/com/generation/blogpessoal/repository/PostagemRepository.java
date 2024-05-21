package com.generation.blogpessoal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.generation.blogpessoal.model.Postagem;

//JpaRepository - classe do JPA (dependência adicionada), dentro dela contém métodos que vão realizar Query no banco. Significa que a JPA assume o controle de criar automaticamente o SELECT * FROM tb_postagem. 
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
// entre o diamante se coloca a classe (postagem) que está puxando da model e o tipo (long). 
}
